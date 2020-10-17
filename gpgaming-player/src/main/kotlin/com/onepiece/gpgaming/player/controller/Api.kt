package com.onepiece.gpgaming.player.controller

import com.onepiece.gpgaming.beans.enums.BannerType
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.value.database.AppVersionValue
import com.onepiece.gpgaming.beans.value.database.BlogValue
import com.onepiece.gpgaming.beans.value.internet.web.SelectCountryResult
import com.onepiece.gpgaming.beans.value.internet.web.ClientConfigValue
import com.onepiece.gpgaming.player.controller.value.ApiValue
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
    fun config(): IndexConfig

//    @ApiOperation(tags = ["api"], value = "首页配置")
//    fun indexConfig(): Index

    @ApiOperation(tags = ["api"], value = "域名配置")
    fun getConfig(): CompileValue.Config

    @ApiOperation(tags = ["api"], value = "热门游戏")
    fun hotGames(): List<HotGameVo>

    @ApiOperation(tags = ["api"],  value = "首页平台列表")
    fun indexPlatforms(): List<PlatformVo>

    @ApiOperation(tags = ["api"], value = "优惠活动(未排序分组)")
    fun promotionList(): List<PromotionVo>

    @ApiOperation(tags = ["api"], value = "优惠活动")
    fun promotion(): List<PromotionVo>

    @ApiOperation(tags = ["api"], value = "代理域名地址")
    fun indexConfig(): CompileValue.AffSite

//    @ApiOperation(tags = ["api"], value = "老虎机菜单")
//    @Deprecated("推荐使用/slots方法")
//    fun slotMenu(
//            @RequestHeader("language") language: Language,
//            @RequestHeader("launch") launch: LaunchMethod,
//            @RequestParam("platform") platform: Platform): Map<String, String>

    @ApiOperation(tags = ["api"], value = "blog")
    fun blogs(): List<BlogValue.BlogMVo>

    @ApiOperation(tags = ["api"], value = "国际化配置")
    fun i18nConfig(@RequestParam("config") config: I18nConfig): List<I18nContent>


    @ApiOperation(tags = ["api"], value = "老虎机游戏列表")
    fun slots(@RequestParam("platform") platform: Platform): List<SlotCategoryVo>


    @ApiOperation(tags = ["api"], value = "开始游戏(平台)")
    fun start(
            @RequestHeader("platform") platform: Platform
    ): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始游戏(平台试玩)")
    fun startDemo(
            @RequestHeader("platform") platform: Platform
    ): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始游戏(老虎机)")
    fun startSlotGame(
            @RequestHeader("platform") platform: Platform,
            @RequestParam("gameId") gameId: String): StartGameResp

    @ApiOperation(tags = ["api"], value = "开始试玩(老虎机)")
    fun startSlotDemoGame(
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
            @RequestParam(value =  "type") type: BannerType
    ): List<BannerVo>

    @ApiOperation(tags = ["api"], value = "获得平台类目页信息")
    fun categories(
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
    fun seo(): ClientConfigValue.ClientConfigVo

    @ApiOperation(tags = ["api"], value = "改变国家")
    fun selectCountry(
            @RequestParam("country") country: Country
    ): SelectCountryResult

    @ApiOperation(tags = ["api"], value = "网站导航配置")
    fun guideConfig(): ApiValue.GuideConfigVo

    @ApiOperation(tags = ["api"], value = "application版本")
    fun checkVersion(): Map<String, AppVersionValue.AppVersionVo>

}
