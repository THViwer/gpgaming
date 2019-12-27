package com.onepiece.gpgaming.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.SystemConstant
import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.Recommended
import com.onepiece.gpgaming.beans.value.internet.web.Index
import com.onepiece.gpgaming.core.service.BannerService
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.RecommendedService
import com.onepiece.gpgaming.utils.AwsS3Util
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
        private val objectMapper: ObjectMapper
) {

    @Autowired
    lateinit var clientService: ClientService

    @Autowired
    lateinit var platformBindService: PlatformBindService


    @Async
    fun generatorIndexPage(clientId: Int) {

        // logo
        val client = clientService.get(clientId)
        val logo = client.logo

        val contents = i18nContentService.getConfigs(clientId)
        val contentMap = contents.map { "${it.configId}:${it.configType}:${it.language}" to it }.toMap()

        // 公告
        val announcements = contents.filter { it.configType == I18nConfig.Announcement }

        // 开通的平台列表
        val binds = platformBindService.findClientPlatforms(clientId)
        val platforms = binds.filter { it.status != Status.Delete }.map {
            val detail = it.platform.detail
            val status = if (detail.status != Status.Normal) detail.status else it.status
            Index.PlatformVo(id = it.id, status = status, open = true, platform = it.platform)
        }.filter { it.status != Status.Delete }

        // banner
        val banners = bannerService.findByType(clientId = clientId, type = BannerType.Banner).filter { it.status == Status.Normal }


        // 推荐列表
        val recommendeds= recommendedService.all(clientId).filter { it.status == Status.Normal }

        // 首页推荐平台
        val recommendedPlatforms = recommendeds.first { it.type == RecommendedType.IndexPlatform }.let {
            val content = it.getRecommendedContent(objectMapper) as Recommended.RecommendedPlatform
            content.platforms.map { platform ->
                Index.RecommendedPlatform(platform = platform)
            }
        }


        Language.values().toList().forEach { language ->

            // 公告
            val announcement = (announcements.firstOrNull{ it.language == language }
                    ?: announcements.first { it.language == Language.EN })
                    .let { it.getII18nContent(objectMapper) as I18nContent.AnnouncementI18n }

            // banner
            val bannerVoList = banners.mapNotNull {

                val data = contentMap["${it.id}:${I18nConfig.Banner}:${language}"]
                        ?: contentMap["${it.id}:${I18nConfig.Banner}:${Language.EN}"]

                data?.let {
                    val content = data.getII18nContent(objectMapper) as I18nContent.BannerI18n
                    Index.BannerVo(icon = content.imagePath)
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
                    Index.SportRecommended(contentImage = data.contentImage, platform = recommendedContent.platform)
                }
            }

            // 首页推荐的真人
            val recommendLives = recommendeds.filter { it.type == RecommendedType.IndexLive }.map {
                val content = it.getRecommendedContent(objectMapper) as Recommended.LiveRecommended
                Index.LiveRecommended(platform = content.platform, contentImage = content.contentImage, title = content.title)
            }

            // 热门游戏
            val hotLanguage = if (language == Language.EN) language else Language.CN
            val hotGameUrl = "${SystemConstant.AWS_SLOT}/hot_sort_20_web_${hotLanguage.name.toLowerCase()}.json"

            val index = Index(logo = logo, platforms = platforms, announcement = announcement, recommendedPlatforms = recommendedPlatforms, lives = recommendLives,
                    banners = bannerVoList, sports = recommendSports, hotGameUrl = hotGameUrl, recommendedVideos = recommendVideos)

            val json = objectMapper.writeValueAsString(index)
            val userHome = System.getProperty("user.home")
            val file = File("$userHome/${UUID.randomUUID()}.json")
            file.writeBytes(json.toByteArray())
            AwsS3Util.uploadLocalFile(file, "client/${client.id}/index_${language.name.toLowerCase()}.json")
            file.delete()
        }
    }

}