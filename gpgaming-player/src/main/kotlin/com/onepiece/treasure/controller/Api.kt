package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.enums.*
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
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestHeader("language") language: Language
    ): ConfigVo

    @ApiOperation(tags = ["api"], value = "平台类别页面详细资料")
    fun categories(
            @PathVariable("category") category: PlatformCategory,
            @RequestHeader("language") language: Language
    ): PlatformCategoryPage

    @ApiOperation(tags = ["api"], value = "优惠活动")
    fun promotion(
            @RequestHeader("language") language: Language,
            @RequestParam("promotionCategory", required = false) promotionCategory: PromotionCategory?
    ): List<PromotionVo>

        @ApiOperation(tags = ["api"], value = "老虎机菜单")
    fun slotMenu(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestParam("platform") platform: Platform): Map<String, String>

    @ApiOperation(tags = ["api"], value = "开始游戏(平台)")
    fun start(
            @RequestHeader("language") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestHeader("launch") launch: LaunchMethod
    ): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始游戏(平台试玩)")
    fun startDemo(
            @RequestHeader("language") language: Language,
            @RequestHeader("platform") platform: Platform,
            @RequestHeader("launch") launch: LaunchMethod
    ): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始游戏(老虎机)")
    fun startSlotGame(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestHeader("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始试玩(老虎机)")
    fun startSlotDemoGame(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestHeader("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp

    @ApiOperation(tags = ["api"], value = "下载客户端(ios或android)")
    fun down(
            @RequestHeader("platform", required = false) platform: Platform?
    ): List<DownloadAppVo>

    @ApiOperation(tags = ["api"], value = "获得游戏平台的账号密码")
    fun platformMemberDetail(@RequestHeader("platform") platform: Platform): PlatformMembrerDetail

    @ApiOperation(tags = ["api"], value = "获得平台类目页信息")
    fun categorys(
            @RequestHeader("language") language: Language,
            @PathVariable("category") category: PlatformCategory
    ): PlatformCategoryDetail

    @ApiOperation(tags = ["api"], value = "联系我们")
    fun contactUs(): Contacts


}