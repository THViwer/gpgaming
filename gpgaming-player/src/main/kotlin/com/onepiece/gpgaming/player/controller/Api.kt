package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.value.internet.web.SelectCountryResult
import com.onepiece.gpgaming.beans.value.internet.web.SeoValue
import com.onepiece.gpgaming.player.controller.value.BannerVo
import com.onepiece.gpgaming.player.controller.value.CompileValue
import com.onepiece.gpgaming.player.controller.value.Contacts
import com.onepiece.gpgaming.player.controller.value.DownloadAppVo
import com.onepiece.gpgaming.player.controller.value.HotGameVo
import com.onepiece.gpgaming.player.controller.value.IndexConfig
import com.onepiece.gpgaming.player.controller.value.PlatformCategoryDetail
import com.onepiece.gpgaming.player.controller.value.PlatformMembrerDetail
import com.onepiece.gpgaming.player.controller.value.PlatformVo
import com.onepiece.gpgaming.player.controller.value.PromotionVo
import com.onepiece.gpgaming.player.controller.value.SlotCategoryVo
import com.onepiece.gpgaming.player.controller.value.StartGameResp
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
    ): IndexConfig

    @ApiOperation(tags = ["api"], value = "域名配置")
    fun getConfig(): CompileValue.Config

    @ApiOperation(tags = ["api"], value = "热门游戏")
    fun hotGames(
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestHeader("language") language: Language
    ): List<HotGameVo>

    @ApiOperation(tags = ["api"],  value = "首页平台列表")
    fun indexPlatforms(
            @RequestHeader("launch", defaultValue = "Wap") launch: LaunchMethod = LaunchMethod.Wap
    ): List<PlatformVo>

    @ApiOperation(tags = ["api"], value = "优惠活动")
    fun promotion(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod
    ): List<PromotionVo>

//    @ApiOperation(tags = ["api"], value = "老虎机菜单")
//    @Deprecated("推荐使用/slots方法")
//    fun slotMenu(
//            @RequestHeader("language") language: Language,
//            @RequestHeader("launch") launch: LaunchMethod,
//            @RequestParam("platform") platform: Platform): Map<String, String>

    @ApiOperation(tags = ["api"], value = "老虎机游戏列表")
    fun slots(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestParam("platform") platform: Platform
    ): List<SlotCategoryVo>


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

    @ApiOperation(tags = ["api"], value = "banner列表")
    fun banners(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @RequestParam(value =  "type") type: BannerType
    ): List<BannerVo>

    @ApiOperation(tags = ["api"], value = "获得平台类目页信息")
    fun categories(
            @RequestHeader("language") language: Language,
            @RequestHeader("launch") launch: LaunchMethod,
            @PathVariable(value =  "category") category: PlatformCategory
    ): PlatformCategoryDetail

//    @ApiOperation(tags = ["api"], value = "平台类别页面详细资料")
//    fun categories(
//            @PathVariable("category") category: PlatformCategory,
//            @RequestHeader("language") language: Language
//    ): PlatformCategoryPage

    @ApiOperation(tags = ["api"], value = "联系我们")
    fun contactUs(): Contacts

    @ApiOperation(tags = ["api"], value = "seo配置")
    fun seo(): SeoValue.SeoVo


    @ApiOperation(tags = ["api"], value = "改变国家")
    fun selectCountry(
            @RequestParam("country") country: Country,
            @RequestHeader("language") language: Language,
            @RequestHeader("launch", defaultValue = "Web") launch: LaunchMethod
    ): SelectCountryResult

}