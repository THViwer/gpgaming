package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.ConfigVo
import com.onepiece.treasure.controller.value.PlatformVo
import com.onepiece.treasure.controller.value.SlotMenu
import com.onepiece.treasure.controller.value.StartGameResp
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.core.service.SlotGameService
import com.onepiece.treasure.games.GameApi
import com.onepiece.treasure.games.value.SlotGame
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ApiController(
        private val platformBindService: PlatformBindService,
        private val slotGameService: SlotGameService,
        private val jokerGameApi: GameApi
) : BasicController(), Api {

    @GetMapping("/{clientId}")
    override fun config(@PathVariable("clientId") clientId: Int): ConfigVo {

        val platformBinds = platformBindService.findClientPlatforms(clientId)
        val platforms = platformBinds.map {
            PlatformVo(id = it.id, name = it.platform.cname, category = it.platform.category, status = it.status)
        }

        return ConfigVo(platforms = platforms)
    }

    @GetMapping("/slot/menu")
    override fun slotMenu(@RequestParam("platform") platform: Platform): List<SlotMenu> {
//        val slotGames = when(platform) {
////
////            Platform.Joker -> {
////                jokerGameApi.games().map {
////                    SlotMenu(gameId = it.gameId, gameName = it.gameName, category = GameCategory.ARCADE, icon = it.icon,
////                            hot = true, new = true, status = Status.Normal)
////                }
////            }
////            else -> slotGameService.findByPlatform(platform)
////        }

        val games = jokerGameApi.games().map {
            SlotMenu(gameId = it.gameId, gameName = it.gameName, category = GameCategory.ARCADE, icon = it.icon,
                    hot = true, new = true, status = Status.Normal)
        }

        return games
//        return slotGames.map{
//            SlotMenu(id = it.id, category = it.category, name = it.name, icon = it.icon, hot = it.hot, new = it.new,
//                status = it.status)
//        }
    }


    @GetMapping("/start/{id}")
    override fun start(@PathVariable("id") id: Int): StartGameResp {
        return StartGameResp(id = id, path = "http://www.baidu.com")
    }

    @GetMapping("/start/slot/{id}")
    override fun startSlotGame(@PathVariable("id") id: Int): StartGameResp {
        return StartGameResp(id = id, path = "http://www.google.com")
    }
}