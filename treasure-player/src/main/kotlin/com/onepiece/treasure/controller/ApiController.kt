package com.onepiece.treasure.controller

import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.ApiValueFactory
import com.onepiece.treasure.controller.value.ConfigVo
import com.onepiece.treasure.controller.value.SlotMenu
import com.onepiece.treasure.controller.value.StartGameResp
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class ApiController : BasicController(), Api {

    @GetMapping
    override fun config(): ConfigVo {
        return ApiValueFactory.generatorConfig()
    }

    @GetMapping("/slot/menu")
    override fun slotMenu(@PathVariable("platformId") platformId: Int): List<SlotMenu> {
        return ApiValueFactory.generatorSlotMenus()
    }


    @GetMapping("/start/{id}")
    override fun start(@PathVariable("id") id: Int): StartGameResp {
        return ApiValueFactory.generatorGameResp()
    }

    @GetMapping("/start/slot/{id}")
    override fun startSlotGame(@PathVariable("id") id: Int): StartGameResp {
        return ApiValueFactory.generatorGameResp()
    }
}