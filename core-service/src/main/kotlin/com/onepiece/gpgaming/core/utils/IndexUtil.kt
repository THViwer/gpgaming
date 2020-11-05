package com.onepiece.gpgaming.core.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.SystemConstant
import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.Recommended
import com.onepiece.gpgaming.beans.value.internet.web.Index
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.core.service.BannerService
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.RecommendedService
import com.onepiece.gpgaming.core.service.WebSiteService
import com.onepiece.gpgaming.utils.AwsS3Util
import com.onepiece.gpgaming.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

@Service
class IndexUtil(
        private val i18nContentService: I18nContentService,
        private val recommendedService: RecommendedService,
        private val bannerService: BannerService,
        private val objectMapper: ObjectMapper,
        private val gamePlatformService: GamePlatformService,
        private val activeConfig: ActiveConfig,
        private val webSiteService: WebSiteService,
        private val redisService: RedisService
) {

    private val log = LoggerFactory.getLogger(IndexUtil::class.java)

    @Autowired
    lateinit var clientService: ClientService

    @Autowired
    lateinit var platformBindService: PlatformBindService


//    fun getIndexConfig(clientId: Int, language: Language, x: Int = 0): Index {
//
//        if (x > 2) error(OnePieceExceptionCode.SYSTEM)
//
//        val redisKey = OnePieceRedisKeyConstant.indexCacheConfig(clientId = clientId, language = language)
//        val index = redisService.get(key = redisKey, clz = Index::class.java)
//
//        return index ?: {
//            this.generatorIndexPage(clientId = clientId)
//            this.getIndexConfig(clientId = clientId, language = language, x = x + 1)
//        }.invoke()
//    }

    @Async
    fun generatorIndexPage(clientId: Int) {

        // logo
        val client = clientService.get(clientId)
        if (client.country == Country.Default) return

        val logo = client.logo

        val contents = i18nContentService.getConfigs(clientId)
        val contentMap = contents.map { "${it.configId}:${it.configType}:${it.language}" to it }.toMap()

        // 绑定平台
        val platformBinds = platformBindService.findClientPlatforms(clientId = clientId)
        val platformBindMap = platformBinds.map { it.platform to it }.toMap()


        // 公告
        val announcements = contents.filter { it.configType == I18nConfig.Announcement }

        val announcementDialogs = contents.filter { it.configType == I18nConfig.AnnouncementDialog }

        // 开通的平台列表
//        val binds = platformBindService.findClientPlatforms(clientId)
        val gamePlatforms = gamePlatformService.all()
//        val platforms = binds.filter { it.status != Status.Delete }.map {
//            val gamePlatform = it.platform.getGamePlatform(gamePlatforms)
//            val status = if (gamePlatform.status != Status.Normal) gamePlatform.status else it.status
//            Index.PlatformVo(id = it.id, status = status, open = true, platform = it.platform)
//        }.filter { it.status != Status.Delete }

        // banner
        val banners = bannerService.findByType(clientId = clientId, type = BannerType.Banner).filter { it.status == Status.Normal }
                .sortedBy { it.order }


        // 推荐列表
        val recommendeds = recommendedService.all(clientId).filter { it.status == Status.Normal }

        // 首页推荐平台
        val recommendedPlatforms = recommendeds.first { it.type == RecommendedType.IndexPlatform }.let {
            val content = it.getRecommendedContent(objectMapper) as Recommended.RecommendedPlatform
            content.platforms.mapNotNull { platform ->
                try {
                    platformBindMap[platform]?.let {
                        Index.RecommendedPlatform(platform = platform, gamePlatform = platform.getGamePlatform(gamePlatforms), platformBind = it)
                    }
                } catch (e: Exception) {
                    log.error("", e)
                    null
                }
            }
        }


        Language.values().toList().forEach { language ->

            // 公告
            val announcement = (announcements.firstOrNull { it.language == language }
                    ?: announcements.first { it.language == Language.EN })
                    .let { it.getII18nContent(objectMapper) as I18nContent.AnnouncementI18n }

            // 公告弹窗
            val announcementDialog = (announcementDialogs.firstOrNull { it.language == language }
                    ?: announcementDialogs.firstOrNull { it.language == Language.EN })?.let { it.getII18nContent(objectMapper) as I18nContent.AnnouncementDialogI18n }
                    ?: I18nContent.AnnouncementDialogI18n(title = "", content = "", nonce = UUID.randomUUID().toString())


            // banner
            val bannerVoList = banners.mapNotNull { banner ->

                val data = contentMap["${banner.id}:${I18nConfig.Banner}:${language}"]
                        ?: contentMap["${banner.id}:${I18nConfig.Banner}:${Language.EN}"]

                data?.let {
                    val content = data.getII18nContent(objectMapper) as I18nContent.BannerI18n
                    Index.BannerVo(pcImagePath = content.pcImagePath, mobileImagePath = content.mobileImagePath, link = banner.link)
                }
            }

            // 首页推荐视频
            val recommendVideos = recommendeds.filter { it.type == RecommendedType.IndexVideo }.mapNotNull {
                val data = contentMap["${it.id}:${I18nConfig.IndexVideo}:${language}"]
                        ?: contentMap["${it.id}:${I18nConfig.IndexVideo}:${Language.EN}"]
                data?.let {
                    val content = data.getII18nContent(objectMapper) as I18nContent.IndexVideoI18n
                    Index.VideoRecommended(path = content.path, introductionImage = content.introductionImage, coverPhoto = content.coverPhoto)
                }
            }


            // 首页推荐的体育
            val recommendSports = recommendeds.filter { it.type == RecommendedType.IndexSport }.mapNotNull {
                val content = contentMap["${it.id}:${I18nConfig.IndexSport}:${language}"]
                        ?: contentMap["${it.id}:${I18nConfig.IndexSport}:${Language.EN}"]

                val recommendedContent = it.getRecommendedContent(objectMapper) as Recommended.RecommendedSport

                content?.let {
                    val data = content.getII18nContent(objectMapper) as I18nContent.IndexSportI18n
                    Index.SportRecommended(contentImage = data.contentImage, platform = recommendedContent.platform, wapContentImage = data.wapContentImage)
                }
            }

            // 首页推荐的真人
            val recommendLives = recommendeds.filter { it.type == RecommendedType.IndexLive }.mapNotNull {
                val content = it.getRecommendedContent(objectMapper) as Recommended.LiveRecommended

                platformBindMap[content.platform]?.let { bind ->
                    Index.LiveRecommended(platform = content.platform, contentImage = content.contentImage, title = content.title,
                            gamePlatform = content.platform.getGamePlatform(gamePlatforms), platformBind = bind)
                }

            }

            // 热门游戏
            val hotLanguage = if (language == Language.EN) language else Language.CN
            val hotGameUrl = "${SystemConstant.AWS_SLOT}/hot_sort_20_web_${hotLanguage.name.toLowerCase()}.json"

            // 代理地址
            val affSite = webSiteService.getAffSite(clientId = client.bossId)?.let {
                "https://aff.${it.domain}"
            }

            val index = Index(logo = logo, announcement = announcement, recommendedPlatforms = recommendedPlatforms, lives = recommendLives,
                    banners = bannerVoList, sports = recommendSports, hotGameUrl = hotGameUrl, recommendedVideos = recommendVideos, name = client.name,
                    shortcutLogo = client.shortcutLogo, affSite = affSite, announcementDialog = announcementDialog)

            // 放到缓存中
//            val redisKey = OnePieceRedisKeyConstant.indexCacheConfig(clientId = clientId, language = language)
//            redisService.put(key = redisKey, value = index)

            val json = objectMapper.writeValueAsString(index)
            val userHome = System.getProperty("user.home")
            val file = File("$userHome/${UUID.randomUUID()}.json")
            file.writeBytes(json.toByteArray())
            AwsS3Util.uploadLocalFile(file, "client/${client.id}/index_${language.name.toLowerCase()}.json", profile = activeConfig.profile)
            file.delete()

        }
    }
}