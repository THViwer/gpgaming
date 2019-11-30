package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.SystemConstant
import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.internet.web.SlotCategory
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
        private val i18nContentService: I18nContentService,
        private val bannerService: BannerService
) : BasicController(), Api {

    @GetMapping
    override fun config(
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestHeader("language", defaultValue = "EN") language: Language
    ): ConfigVo {

        val clientId = this.getClientIdByDomain()

        // 平台信息
        val platformBinds = platformBindService.findClientPlatforms(clientId)
        val platforms = platformBinds.map {
            PlatformVo(id = it.id, name = it.platform.detail.name, category = it.platform.detail.category, status = it.status, icon = it.platform.detail.icon,
                    launchs = it.platform.detail.launchs, platform = it.platform)
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
        val hotGameUrl = when (launch) {
            LaunchMethod.Web -> "${SystemConstant.AWS_SLOT}/hot_sort_10_wap.json"
            else -> "${SystemConstant.AWS_SLOT}/hot_sort_20_web.json"
        }

        return ConfigVo(platforms = platforms, announcementVo = announcementVo, banners = banners, hotGameUrl = hotGameUrl)
    }

    @GetMapping("/promotion")
    override fun promotion(
            @RequestHeader("language", defaultValue = "EN") language: Language
    ): List<PromotionVo> {

        val clientId = getClientIdByDomain()
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
            @RequestParam("platform") platform: Platform): Map<String, String> {

        val url = when(platform) {
            Platform.Joker -> "${SystemConstant.AWS_SLOT}/joker_${launch.name.toLowerCase()}.json"
            Platform.MicroGaming -> "${SystemConstant.AWS_SLOT}/micro_gaming.json"
            Platform.Pragmatic -> "${SystemConstant.AWS_SLOT}/pragmatic_${launch.name.toLowerCase()}.json"
            Platform.SpadeGaming -> "${SystemConstant.AWS_SLOT}/spade_game.json"
            Platform.TTG -> "${SystemConstant.AWS_SLOT}/ttg_${launch.name.toLowerCase()}.json"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
        return mapOf(
                "url" to url
        )
    }

    @GetMapping("/start")
    override fun start(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod
    ): StartGameResp {
        val member = current()
        val platformMember = getPlatformMember(platform)

        val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform,
                launch = launch, language = language, platformPassword = platformMember.platformPassword)
        return StartGameResp(path = gameUrl)
    }

    @GetMapping("/demo")
    override fun startDemo(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod
    ): StartGameResp {
        val url = gameApi.startDemo(clientId = getClientIdByDomain(), platform = platform, language = language, launch = launch)

        return StartGameResp(path = url)
    }

    @GetMapping("/start/slot")
    override fun startSlotGame(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestParam("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp {

        val platformMember = getPlatformMember(platform)
        val member = current()

        val gameUrl = gameApi.start(clientId = member.clientId, platformUsername = platformMember.platformUsername, platform = platform,
                gameId = gameId, language = language, launchMethod = launch)
        return StartGameResp(path = gameUrl)

    }

    @GetMapping("/start/slot/demo")
    override fun startSlotDemoGame(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestParam("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp {

        val gameUrl = gameApi.startSlotDemo(clientId = getClientIdByDomain(), platform = platform, gameId = gameId, language = language,
                launchMethod = launch)
        return StartGameResp(path = gameUrl)
    }

    @GetMapping("/down")
    override fun down(@RequestHeader("platform", required = false) platform: Platform?): List<DownloadAppVo> {
        //TODO 暂时不解析网站

        // kiss918 -> https://www.918kisse.com/

        // pussy888 -> https://918kiss.care/pussy888/

        // Mega -> http://m.mega585.com/ 可能需要通过接口

        val kiss918 = DownloadAppVo(platform = Platform.Kiss918, icon = Platform.Kiss918.detail.icon,
                iosPath = "itms-services://?action=download-manifest&url=https://s3-ap-southeast-1.amazonaws.com/app918kiss/ios/918Kiss.plist",
                androidPath = "https://s3-ap-southeast-1.amazonaws.com/app918kiss/apk/918Kiss.apk")

        val pussy888 = DownloadAppVo(platform = Platform.Pussy888, icon = Platform.Pussy888.detail.icon,
                iosPath = "itms-services://?action=download-manifest&url=https://pussy888.s3.amazonaws.com/pussy888/appsetup/ios/pussy888.plist",
                androidPath = "https://s3-ap-southeast-1.amazonaws.com/pussy888/pussy888/appsetup/apk/pussy888.apk")

        val mega = DownloadAppVo(platform = Platform.Mega, icon = Platform.Mega.detail.icon,
                iosPath = "itms-services://?action=download-manifest&url=https://aka-dd-mega-appsetup.siderby.com/ios/Mega888.plist",
                androidPath = "https://aka-dd-mega-appsetup.siderby.com/apk/Mega888_V1.2.apk")

        val list = listOf(kiss918, pussy888, mega)
        return list.filter { platform == null || platform == it.platform }

    }

    @GetMapping("/platform/member")
    override fun platformMemberDetail(@RequestHeader("platform") platform: Platform): PlatformMembrerDetail {
        return getPlatformMember(platform).let {
            PlatformMembrerDetail(username = it.platformUsername, password = it.platformPassword)
        }
    }

    @GetMapping("/{category}")
    override fun categorys(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @PathVariable("category") category: PlatformCategory
    ): PlatformCategoryDetail {

        val clientId = this.getClientIdByDomain()

        val platforms = platformBindService.findClientPlatforms(clientId = getClientIdByDomain())
                .filter { it.platform.detail.category == category }
                .map {
                    PlatformVo(id = it.id, platform = it.platform, name = it.platform.detail.name, category = it.platform.detail.category,
                            status = it.platform.detail.status, icon = it.platform.detail.icon, launchs = it.platform.detail.launchs)
                }

        val type = when (category) {
            PlatformCategory.Fishing -> BannerType.Fish
            PlatformCategory.Slot -> BannerType.Slot
            PlatformCategory.LiveVideo -> BannerType.Live
            PlatformCategory.Sport -> BannerType.Sport
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        val banners = bannerService.findByType(clientId = clientId, type = type).map {
            BannerVo(id = it.id, order = it.order, icon = it.icon, link = it.link, touchIcon = it.touchIcon, type = it.type)
        }

        val games = if (category == PlatformCategory.Slot) {
            gameApi.slotGames(clientId = getClientIdByDomain(), platform = Platform.Joker, launch = LaunchMethod.Web)
                    .groupBy { it.category }
                    .map {
                        SlotCategory(gameCategory = it.key, games = it.value)
                    }
        } else null


        return PlatformCategoryDetail(platforms = platforms, banners = banners, games = games)



    }
}