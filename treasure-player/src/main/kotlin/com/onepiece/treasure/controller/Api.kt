package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.controller.value.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Api(tags = ["api"], description = " ")
interface Api {

    @ApiOperation(tags = ["api"], value = "首页配置")
    fun config(@RequestHeader("clientId") clientId: Int): ConfigVo

    @ApiOperation(tags = ["api"], value = "优惠活动")
    fun promotion(@RequestHeader("clientId") clientId: Int): List<PromotionVo>

    @ApiOperation(tags = ["api"], value = "老虎机菜单")
    fun slotMenu(@RequestParam("platform") platform: Platform): List<SlotMenu>

    @ApiOperation(tags = ["api"], value = "开始游戏(平台)")
    fun start(@RequestHeader("platform") platform: Platform): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始游戏(老虎机)")
    fun startSlotGame(@RequestHeader("platform") platform: Platform,
                      @RequestParam("gameId") gameId: String): StartGameResp

    @ApiOperation(tags = ["api"], value = "下载客户端(ios或android)")
    fun down(@PathVariable("mobilePlatform") mobilePlatform: String): List<DownloadAppVo>

    @ApiOperation(tags = ["api"], value = "获得游戏平台的账号密码")
    fun platformMemberDetail(@RequestHeader("platform") platform: Platform): PlatformMembrerDetail


}