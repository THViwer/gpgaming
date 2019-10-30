package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.controller.value.ConfigVo
import com.onepiece.treasure.controller.value.SlotMenu
import com.onepiece.treasure.controller.value.StartGameResp
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Api(tags = ["api"], description = " ")
interface Api {

    @ApiOperation(tags = ["api"], value = "config")
    fun config(@PathVariable("clientId") clientId: Int): ConfigVo

    @ApiOperation(tags = ["api"], value = "slot menu")
    fun slotMenu(@RequestParam("platform") platform: Platform): List<SlotMenu>

    @ApiOperation(tags = ["api"], value = "start game")
    fun start(@PathVariable("id") id: Int): StartGameResp

    @ApiOperation(tags = ["api"], value = "start slot game")
    fun startSlotGame(@RequestParam("platform") platform: Platform,
                      @RequestParam("gameId") gameId: String): StartGameResp


}