package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.HotGameType
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.ShowPosition
import com.onepiece.gpgaming.beans.model.Contact
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.Recommended
import com.onepiece.gpgaming.beans.model.Seo
import com.onepiece.gpgaming.beans.value.database.BlogValue
import com.onepiece.gpgaming.beans.value.database.HotGameValue
import com.onepiece.gpgaming.beans.value.internet.web.BannerCoReq
import com.onepiece.gpgaming.beans.value.internet.web.BannerUoReq
import com.onepiece.gpgaming.beans.value.internet.web.BannerVo
import com.onepiece.gpgaming.beans.value.internet.web.ContactValue
import com.onepiece.gpgaming.beans.value.internet.web.HotGameVo
import com.onepiece.gpgaming.beans.value.internet.web.I18nContentWebValue
import com.onepiece.gpgaming.beans.value.internet.web.PromotionCoReq
import com.onepiece.gpgaming.beans.value.internet.web.PromotionUoReq
import com.onepiece.gpgaming.beans.value.internet.web.PromotionVo
import com.onepiece.gpgaming.beans.value.internet.web.RecommendedWebValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["web setting"], description = "网站设置")
interface IndexApi {

    @ApiOperation(tags = ["web setting"], value = "seo -> 获取")
    fun seo(): Seo

    @ApiOperation(tags = ["web setting"], value = "seo -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun seo(
            @RequestParam("title") title: String,
            @RequestParam("keywords") keywords: String,
            @RequestParam("description") description: String,
            @RequestParam("liveChatId") liveChatId: String,
            @RequestParam("liveChatTab") liveChatTab: Boolean,
            @RequestParam("googleStatisticsId") googleStatisticsId: String,
            @RequestParam("facebookTr") facebookTr:String,
            @RequestParam("facebookShowPosition") facebookShowPosition: ShowPosition,
            @RequestParam("asgContent") asgContent: String
    )


    @ApiOperation(tags = ["web setting"], value = "支持语言列表")
    fun languages(): List<Language>

    @ApiOperation(tags = ["web setting"], value = "公告 -> 列表")
    fun announcementList(): List<I18nContent>

    @ApiOperation(tags = ["web setting"], value = "国际化 -> 内容创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody i18nContentCoReq: I18nContentWebValue.I18nContentCoReq)

    @ApiOperation(tags = ["web setting"], value = "国际化 -> 内容更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody i18nContentUoReq: I18nContentWebValue.I18nContentUoReq)

    @ApiOperation(tags = ["web setting"], value = "国际化 -> 列表")
    fun list(@RequestParam("config") config: I18nConfig): List<I18nContent>



    @ApiOperation(tags = ["web setting"], value = "首页设置 -> 列表")
    fun bannerList(): List<BannerVo>

    @ApiOperation(tags = ["web setting"], value = "首页设置 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody bannerCoReq: BannerCoReq)

    @ApiOperation(tags = ["web setting"], value = "首页设置 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody bannerUoReq: BannerUoReq)




    @ApiOperation(tags = ["web setting"], value = "优惠活动 -> 列表")
    fun promotionList(): List<PromotionVo>

    @ApiOperation(tags = ["web setting"], value = "优惠活动 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody promotionCoReq: PromotionCoReq)

    @ApiOperation(tags = ["web setting"], value = "优惠活动 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody promotionUoReq: PromotionUoReq)



    @ApiOperation(tags = ["web setting"], value = "推荐 -> 列表")
    fun recommendedList(
            @RequestParam("type") type: RecommendedType
    ): List<Recommended>

    @ApiOperation(tags = ["web setting"], value = "推荐 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody coReq: RecommendedWebValue.CreateReq)

    @ApiOperation(tags = ["web setting"], value = "推荐 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody uoReq: RecommendedWebValue.UpdateReq)



    @ApiOperation(tags = ["web setting"], value = "热门游戏 -> 列表")
    fun hotGameList(@RequestParam("type") type: HotGameType): List<HotGameVo>

    @ApiOperation(tags = ["web setting"], value = "热门游戏 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody hotGameCo: HotGameValue.HotGameCo)

    @ApiOperation(tags = ["web setting"], value = "热门游戏 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody hotGameUo: HotGameValue.HotGameUo)


    @ApiOperation(tags = ["web setting"], value = "blog -> 列表")
    fun blogList(): List<BlogValue.BlogVo>

    @ApiOperation(tags = ["web setting"], value = "blog -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun blogCreate(@RequestBody blogCo: BlogValue.BlogCo)

    @ApiOperation(tags = ["web setting"], value = "blog -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun blogUpdate(@RequestBody blogUo: BlogValue.BlogUo)


    @ApiOperation(tags = ["web setting"], value = "代理计划 -> 列表")
    fun agentPlats(): List<I18nContent>


    @ApiOperation(tags = ["web setting"], value = "联系我们 -> 列表")
    fun all(@RequestParam("role", defaultValue = "Member") role: Role): List<Contact>

    @ApiOperation(tags = ["web setting"], value = "联系我们 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody create: ContactValue.Create)

    @ApiOperation(tags = ["web setting"], value = "联系我们 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody update: ContactValue.Update)

}