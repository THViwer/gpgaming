package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["web setting"], description = " ")
interface PromotionApi  {

    @ApiOperation(tags = ["web setting"], value = "优惠活动 -> 列表")
    fun all(): List<PromotionVo>

    @ApiOperation(tags = ["web setting"], value = "优惠活动 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody promotionCoReq: PromotionCoReq)

    @ApiOperation(tags = ["web setting"], value = "优惠活动 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody promotionUoReq: PromotionUoReq)


}