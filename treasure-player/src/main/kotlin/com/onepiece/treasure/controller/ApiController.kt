package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.StartPlatform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import com.onepiece.treasure.core.service.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ApiController(
        private val platformBindService: PlatformBindService,
        private val promotionService: PromotionService,
        private val advertService: AdvertService,
        private val announcementService: AnnouncementService
) : BasicController(), Api {

    @GetMapping
    override fun config(@RequestHeader("clientId") clientId: Int): ConfigVo {

        val platformBinds = platformBindService.findClientPlatforms(clientId)
        val platforms = platformBinds.map {
            PlatformVo(id = it.id, name = it.platform.cname, category = it.platform.category, status = it.status, icon = it.platform.icon,
                    starts = it.platform.starts)
        }

        val announcementVo = announcementService.last(clientId)?.let {
            AnnouncementVo(title = it.title, content = it.content, createdTime = it.createdTime)
        }

        val adverts = advertService.all(clientId).map {
            AdvertVo(id = it.id, order = it.order, icon = it.icon, touchIcon = it.touchIcon, position = it.position, link = it.link)
        }

        return ConfigVo(platforms = platforms, announcementVo = announcementVo, adverts = adverts)
    }

    @GetMapping("/promotion")
    override fun promotion(@RequestHeader("clientId") clientId: Int): List<PromotionVo> {
        val promotions = promotionService.all(clientId)
        return promotions.map {
            PromotionVo(id = it.id, clientId = it.clientId, category = it.category, stopTime = it.stopTime, top = it.top, icon = it.icon,
                    title = it.title, synopsis = it.synopsis, content = it.content, status = it.status, createdTime = it.createdTime)
        }

    }

    @GetMapping("/slot/menu")
    override fun slotMenu(@RequestParam("platform") platform: Platform): List<SlotMenu> {

        return gamePlatformUtil.getPlatformBuild(platform).gameApi.games().map {
            SlotMenu(gameId = it.gameId, gameName = it.gameName, category = GameCategory.ARCADE, icon = it.icon,
                    hot = true, new = true, status = Status.Normal)
        }
    }

    @GetMapping("/start")
    override fun start(@RequestHeader("platform") platform: Platform): StartGameResp {
        val platformMember = getPlatformMember(platform)

        val map = gamePlatformUtil.getPlatformBuild(platform).gameApi.start(platformMember.platformUsername, platformMember.platformPassword)
        return StartGameResp(path = map[StartPlatform.Pc] ?: error(""))
    }


    @GetMapping("/start/slot")
    override fun startSlotGame(
            @RequestParam("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp {

        val platformMember = getPlatformMember(platform)

        val maps = gamePlatformUtil.getPlatformBuild(platform).gameApi
                .start(username = platformMember.platformUsername, gameId = gameId, redirectUrl = "http://www.baidu.com")

        return StartGameResp(path = maps[StartPlatform.Pc] ?: error(""))
    }


    @GetMapping("/down/{mobilePlatform}")
    override fun down(@PathVariable("mobilePlatform") mobilePlatform: String): List<DownloadAppVo> {
        return when (mobilePlatform) {
            "ios" -> {
                val joker = DownloadAppVo(Platform.Joker, Platform.Joker.icon, "http//:www.baidu.com")
                val cta666 = DownloadAppVo(Platform.Cta666, Platform.Cta666.icon, "http//:www.baidu.com")
                listOf(joker, cta666)
            }
            else -> {
                val joker = DownloadAppVo(Platform.Joker, Platform.Joker.icon, "http//:www.baidu.com")
                val cta666 = DownloadAppVo(Platform.Cta666, Platform.Cta666.icon, "http//:www.baidu.com")
                listOf(joker, cta666)
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