package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.*
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["index"], description = " ")
interface PromotionApi  {

    @ApiOperation(tags = ["index"], value = "")
    fun all(): List<PromotionVo>

    @ApiOperation(tags = ["index"], value = "")
    fun create(@RequestBody promotionCoReq: PromotionCoReq)

    @ApiOperation(tags = ["index"], value = "")

    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody promotionUoReq: PromotionUoReq)


}