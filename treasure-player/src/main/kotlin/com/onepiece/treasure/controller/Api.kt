package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.controller.value.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Api(tags = ["api"], description = " ")
interface Api {

    @ApiOperation(tags = ["api"], value = "config")
    fun config(@RequestHeader("clientId") clientId: Int): ConfigVo

    fun promotion(@RequestHeader("clientId") clientId: Int): List<PromotionVo>

    @ApiOperation(tags = ["api"], value = "slot menu")
    fun slotMenu(@RequestParam("platform") platform: Platform): List<SlotMenu>

    @ApiOperation(tags = ["api"], value = "start game")
    fun start(@RequestHeader("platform") platform: Platform): StartGameResp

    @ApiOperation(tags = ["api"], value = "start slot game")
    fun startSlotGame(@RequestHeader("platform") platform: Platform,
                      @RequestParam("gameId") gameId: String): StartGameResp
    @ApiOperation(tags = ["api"], value = "down app game (ios or android)")
    fun down(@PathVariable("mobilePlatform") mobilePlatform: String): List<DownloadAppVo>


}