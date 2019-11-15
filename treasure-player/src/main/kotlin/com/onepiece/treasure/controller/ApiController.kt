package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
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
            @RequestHeader("clientId") clientId: Int,
            @RequestHeader("language", defaultValue = "EN") language: Language
    ): List<PromotionVo> {

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
            @RequestParam("platform") platform: Platform): List<SlotMenu> {

        val member = current()

        return gameApi.slotGames(clientId = member.clientId, platform = platform).map {
            SlotMenu(gameId = it.gameId, gameName = it.gameName, category = GameCategory.ARCADE, icon = it.icon,
                    hot = true, new = true, status = Status.Normal)
        }
    }

    @GetMapping("/start")
    override fun start(
            @RequestHeader("platform") platform: Platform,
            @RequestParam(value = "startPlatform", defaultValue = "Pc") startPlatform: LaunchMethod
    ): StartGameResp {
        val platformMember = getPlatformMember(platform)

        val member = current()
        val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform,
                startPlatform = startPlatform)
        return StartGameResp(path = gameUrl)
    }

    @GetMapping("/demo")
    override fun startDemo(
            @RequestHeader("platform") platform: Platform,
            @RequestParam(value = "startPlatform", defaultValue = "Pc") startPlatform: LaunchMethod): StartGameResp {

        val url = when {
            platform == Platform.Lbc && startPlatform == LaunchMethod.Pc-> "http://c.gsoft888.net/vender.aspx?lang=en&OType=1&skincolor=bl001"
            platform == Platform.Lbc && startPlatform == LaunchMethod.Wap-> "https://i.gsoft888.net/vender.aspx?lang=en&OType=1&skincolor=bl001&ischinaview=True&homeUrl=http://localhost/1/&singupUrl=http://localhost/2/&LoginUrl=http://localhost/3/"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        return StartGameResp(path = url)
    }

    @GetMapping("/start/slot")
    override fun startSlotGame(
            @RequestParam("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp {

        val platformMember = getPlatformMember(platform)
        val member = current()

        val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform, gameId = gameId)
        return StartGameResp(path = gameUrl)

    }

    @GetMapping("/down/{mobilePlatform}")
    override fun down(@PathVariable("mobilePlatform") mobilePlatform: String): List<DownloadAppVo> {
        return when (mobilePlatform) {
            "ios" -> {
                val joker = DownloadAppVo(Platform.Joker, Platform.Joker.detail.icon, "http//:www.baidu.com")
                val ct = DownloadAppVo(Platform.CT, Platform.CT.detail.icon, "http//:www.baidu.com")
                listOf(joker, ct)
            }
            else -> {
                val joker = DownloadAppVo(Platform.Joker, Platform.Joker.detail.icon, "http//:www.baidu.com")
                val ct = DownloadAppVo(Platform.CT, Platform.CT.detail.icon, "http//:www.baidu.com")
                listOf(joker, ct)
            }
        }
    }

    @GetMapping("/platform/member")
    override fun platformMemberDetail(@RequestHeader("platform") platform: Platform): PlatformMembrerDetail {
        return getPlatformMember(platform).let {
            PlatformMembrerDetail(username = it.platformUsername, password = it.platformPassword)
        }

    }
}