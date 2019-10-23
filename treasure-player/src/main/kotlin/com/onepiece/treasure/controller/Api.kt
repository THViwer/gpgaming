package com.onepiece.treasure.controller

import com.onepiece.treasure.controller.value.ConfigVo
import com.onepiece.treasure.controller.value.SlotMenu
import com.onepiece.treasure.controller.value.StartGameResp
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PathVariable

@Api(tags = ["api"], description = " ")
interface Api {

    @ApiOperation(tags = ["api"], value = "config")
    fun config(): ConfigVo

    @ApiOperation(tags = ["api"], value = "slot menu")
    fun slotMenu(@PathVariable("platformId") platformId: Int): List<SlotMenu>

    @ApiOperation(tags = ["api"], value = "start game")
    fun start(@PathVariable("id") id: Int): StartGameResp

    @ApiOperation(tags = ["api"], value = "start slot game")
    fun startSlotGame(@PathVariable("id") id: Int): StartGameResp


}