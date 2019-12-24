package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.Recommended
import com.onepiece.gpgaming.beans.value.internet.web.BannerCoReq
import com.onepiece.gpgaming.beans.value.internet.web.BannerUoReq
import com.onepiece.gpgaming.beans.value.internet.web.BannerVo
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
    fun updagte(@RequestBody uoReq: RecommendedWebValue.UpdateReq)



}