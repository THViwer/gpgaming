package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import com.onepiece.treasure.core.service.BannerService
import com.onepiece.treasure.core.service.I18nContentService
import com.onepiece.treasure.core.service.PromotionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ApiController(
        private val promotionService: PromotionService,
        private val advertService: BannerService,
//        private val announcementService: AnnouncementService,
        private val i18nContentService: I18nContentService
) : BasicController(), Api {

    @GetMapping
    override fun config(
            @RequestHeader("language", defaultValue = "EN") language: Language
    ): ConfigVo {

        val clientId = this.getClientIdByDomain()

        // 平台信息
        val platformBinds = platformBindService.findClientPlatforms(clientId)
        val platforms = platformBinds.map {
            PlatformVo(id = it.id, name = it.platform.detail.name, category = it.platform.detail.category, status = it.status, icon = it.platform.detail.icon,
                    launchs = it.platform.detail.launchs)
        }

        // 公告
        val contents = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Announcement)
        val announcement = contents.firstOrNull { it.language == language }?: contents.find { it.language == Language.EN } ?: contents.first()
        val announcementVo = AnnouncementVo(title = announcement.title, content = announcement.content, synopsis = announcement.synopsis)

        // 获得首页配置
        val bannerMap = advertService.all(clientId).map {
            BannerVo(id = it.id, order = it.order, icon = it.icon, touchIcon = it.touchIcon, type = it.type, link = it.link)
        }.groupBy { it.type }

        // banners
        val banners = bannerMap[BannerType.Banner]?: emptyList()
        // hot games
        val hotGames = HotGameVo.of()

        return ConfigVo(platforms = platforms, announcementVo = announcementVo, banners = banners, hotGames = hotGames)
    }

    @GetMapping("/promotion")
    override fun promotion(
            @RequestHeader("language", defaultValue = "EN") language: Language
    ): List<PromotionVo> {

        val clientId = 1

        val promotions = promotionService.all(clientId)

        val i18nContentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Promotion)
                .map { "${it.configId}_${it.language}" to it }
                .toMap()

        return promotions.map {

            val i18nContent = i18nContentMap["${it.id}_${language}"] ?: i18nContentMap["${it.id}_${Language.EN}"] ?: error(OnePieceExceptionCode.LANGUAGE_CONFIG_FAIL)

            PromotionVo(id = it.id, clientId = it.clientId, category = it.category, stopTime = it.stopTime, top = it.top, icon = it.icon,
                    title = i18nContent.title, synopsis = i18nContent.synopsis, content = i18nContent.content, status = it.status, createdTime = it.createdTime)
        }

    }

    @GetMapping("/slot/menu")
    override fun slotMenu(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestParam("platform") platform: Platform): List<SlotGame> {

        val member = current()

        return gameApi.slotGames(clientId = member.clientId, platform = platform, launch = launch)
    }

    @GetMapping("/start")
    override fun start(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestParam(value = "startPlatform", defaultValue = "Pc") startPlatform: LaunchMethod
    ): StartGameResp {
        val platformMember = getPlatformMember(platform)

        val member = current()
        val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform,
                startPlatform = startPlatform, language = language)
        return StartGameResp(path = gameUrl)
    }

    @GetMapping("/demo")
    override fun startDemo(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestParam(value = "startPlatform", defaultValue = "Pc") startPlatform: LaunchMethod): StartGameResp {

        val url = when {
            platform == Platform.Lbc && startPlatform == LaunchMethod.Web-> "http://c.gsoft888.net/vender.aspx?lang=en&OType=1&skincolor=bl001"
            platform == Platform.Lbc && startPlatform == LaunchMethod.Wap-> "https://i.gsoft888.net/vender.aspx?lang=en&OType=1&skincolor=bl001&ischinaview=True&homeUrl=http://localhost/1/&singupUrl=http://localhost/2/&LoginUrl=http://localhost/3/"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        return StartGameResp(path = url)
    }

    @GetMapping("/start/slot")
    override fun startSlotGame(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestParam("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp {

        val platformMember = getPlatformMember(platform)
        val member = current()

        val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform,
                gameId = gameId, language = language)
        return StartGameResp(path = gameUrl)

    }

    @GetMapping("/down/{mobilePlatform}")
    override fun down(@PathVariable("mobilePlatform") mobilePlatform: String): List<DownloadAppVo> {
        //TODO 暂时不解析网站

        // kiss918 -> https://www.918kisse.com/

        // pussy888 -> https://918kiss.care/pussy888/

        // Mega -> http://m.mega585.com/ 可能需要通过接口



        return when (mobilePlatform) {
            "ios" -> {
                val kiss918 = DownloadAppVo(Platform.Kiss918, Platform.Kiss918.detail.icon, "itms-services://?action=download-manifest&url=https://s3-ap-southeast-1.amazonaws.com/app918kiss/ios/918Kiss.plist")
                val pussy888 = DownloadAppVo(Platform.Pussy888, Platform.Pussy888.detail.icon, "itms-services://?action=download-manifest&url=https://pussy888.s3.amazonaws.com/pussy888/appsetup/ios/pussy888.plist")
                val mega = DownloadAppVo(Platform.Mega, Platform.Mega.detail.icon, "itms-services://?action=download-manifest&url=https://aka-dd-mega-appsetup.siderby.com/ios/Mega888.plist")
                listOf(kiss918, pussy888, mega)
            }
            "android" -> {
                val kiss918 = DownloadAppVo(Platform.Kiss918, Platform.Kiss918.detail.icon, "https://s3-ap-southeast-1.amazonaws.com/app918kiss/apk/918Kiss.apk")
                val pussy888 = DownloadAppVo(Platform.Pussy888, Platform.Pussy888.detail.icon, "https://s3-ap-southeast-1.amazonaws.com/pussy888/pussy888/appsetup/apk/pussy888.apk")
                val mega = DownloadAppVo(Platform.Mega, Platform.Mega.detail.icon, "https://aka-dd-mega-appsetup.siderby.com/apk/Mega888_V1.2.apk")
                listOf(kiss918, pussy888, mega)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

    @GetMapping("/platform/member")
    override fun platformMemberDetail(@RequestHeader("platform") platform: Platform): PlatformMembrerDetail {
        return getPlatformMember(platform).let {
            PlatformMembrerDetail(username = it.platformUsername, password = it.platformPassword)
        }

    }
}