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
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

@Service
class IndexUtil(
        private val clientService: ClientService,
        private val i18nContentService: I18nContentService,
        private val platformBindService: PlatformBindService,
        private val recommendedService: RecommendedService,
        private val bannerService: BannerService,
        private val objectMapper: ObjectMapper
) {

    @Async
    fun generatorIndexPage(clientId: Int) {

        // logo
        val client = clientService.get(clientId)
        val logo = client.logo

        val contents = i18nContentService.getConfigs(clientId)
        val contentMap = contents.map { "${it.configType}:${it.language}" to it }.toMap()

        // 公告
        val announcements = contents.filter { it.configType == I18nConfig.Announcement }

        // 开通的平台列表
        val binds = platformBindService.findClientPlatforms(clientId)
        val platforms = binds.filter { it.status != Status.Delete }.map {
            val detail = it.platform.detail
            val status = if (detail.status != Status.Normal) detail.status else it.status
            Index.PlatformVo(id = it.id, category = detail.category, logo = detail.icon, name = detail.name, status = status, open = true)
        }

        // banner
        val banners = bannerService.findByType(clientId = clientId, type = BannerType.Banner)


        // 推荐列表
        val recommendeds= recommendedService.all(clientId).filter { it.status == Status.Normal }

        // 首页推荐平台
        val recommentPlatforms = recommendeds.filter { it.type == RecommendedType.IndexPlatform }.map {
            it.getRecommendedContent(objectMapper) as Recommended.RecommendedPlatform

        }


        Language.values().forEach { language ->



            // 公告
            val announcement = (announcements.firstOrNull{ it.language == language }
                    ?: announcements.first { it.language == Language.EN })
                    .let { it.getII18nContent(objectMapper) as I18nContent.AnnouncementI18n }

            // banner
            val bannerVoList = banners.mapNotNull {
                contentMap["${I18nConfig.Banner}:${language}"]
            }.map {
                val content = it.getII18nContent(objectMapper) as I18nContent.BannerI18n
                Index.BannerVo(icon = content.imagePath)
            }

            // 首页推荐视频
            val recommendVideos = recommendeds.filter { it.type == RecommendedType.IndexVideo }.mapNotNull {
                contentMap["${I18nConfig.IndexVideo}:${language}"]
            }.map {
                val content = it.getII18nContent(objectMapper) as I18nContent.IndexVideoI18n
                Recommended.VideoRecommended(path = content.videoPath, introductionImage = content.introductionImage)
            }

            // 首页推荐的体育
            val recommendSports = recommendeds.filter { it.type == RecommendedType.IndexSport }.map {
                val content = contentMap["${I18nConfig.IndexSport}:${language}"]

                if (content != null) {
                    val i18nContent = content.getII18nContent(objectMapper) as I18nContent.IndexSportI18n
                    Recommended.SportRecommended(contentImage = i18nContent.imagePath)
                } else null

            }.filterNotNull()


            // 首页推荐的真人
            val recommendLives = recommendeds.filter { it.type == RecommendedType.IndexLive }.map {
                it.getRecommendedContent(objectMapper) as Recommended.LiveRecommended
            }

            // 热门游戏
            val hotLanguage = if (language == Language.EN) language else Language.EN
            val hotGameUrl = "${SystemConstant.AWS_SLOT}/hot_sort_20_web_${hotLanguage.name.toLowerCase()}.json"

            val index = Index(logo = logo, platforms = platforms, announcement = announcement, recommendedPlatforms = recommentPlatforms, lives = recommendLives,
                    banners = bannerVoList, sports = recommendSports, hotGameUrl = hotGameUrl, recommendedVideos = recommendVideos)

            val json = objectMapper.writeValueAsString(index)
            val userHome = System.getProperty("user.home")
            val file = File("$userHome/${UUID.randomUUID()}.json")
            file.writeBytes(json.toByteArray())
            AwsS3Util.uploadLocalFile(file, "index/$client/index.json")
            file.delete()
        }
    }

}