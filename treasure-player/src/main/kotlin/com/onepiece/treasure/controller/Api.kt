package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.LaunchMethod
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.PlatformCategory
import com.onepiece.treasure.beans.value.internet.web.SlotCategory
import com.onepiece.treasure.beans.value.internet.web.SlotGame
import com.onepiece.treasure.controller.value.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Api(tags = ["api"], description = " ")
interface Api {

    @ApiOperation(tags = ["api"], value = "首页配置")
    fun config(
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestHeader("language", defaultValue = "EN") language: Language): ConfigVo


    @ApiOperation(tags = ["api"], value = "优惠活动")
    fun promotion(
            @RequestHeader("language", defaultValue = "EN") language: Language
    ): List<PromotionVo>

    @ApiOperation(tags = ["api"], value = "老虎机菜单")
    fun slotMenu(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestParam("platform") platform: Platform): Map<String, String>

    @ApiOperation(tags = ["api"], value = "开始游戏(平台)")
    fun start(
            @RequestHeader("language", defaultValue = "EN") language: Language,

            @RequestHeader("platform") platform: Platform,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod
    ): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始游戏(平台试玩)")
    fun startDemo(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod
    ): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始游戏(老虎机)")
    fun startSlotGame(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestHeader("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始试玩(老虎机)")
    fun startSlotDemoGame(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod,
            @RequestHeader("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp

    @ApiOperation(tags = ["api"], value = "下载客户端(ios或android)")
    fun down(): List<DownloadAppVo>

    @ApiOperation(tags = ["api"], value = "获得游戏平台的账号密码")
    fun platformMemberDetail(@RequestHeader("platform") platform: Platform): PlatformMembrerDetail

    @ApiOperation(tags = ["api"], value = "获得平台类目页信息")
    fun categorys(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @PathVariable("category") category: PlatformCategory
    ): PlatformCategoryDetail


}