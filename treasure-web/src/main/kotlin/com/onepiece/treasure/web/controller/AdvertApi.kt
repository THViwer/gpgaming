package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.AdvertCoReq
import com.onepiece.treasure.beans.value.internet.web.AdvertUoReq
import com.onepiece.treasure.beans.value.internet.web.AdvertVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["index"], description = " ")
interface AdvertApi  {

    @ApiOperation(tags = ["index"], value = "")
    fun all(): List<AdvertVo>

    @ApiOperation(tags = ["index"], value = "")
    fun create(@RequestBody advertCoReq: AdvertCoReq)

    @ApiOperation(tags = ["index"], value = "")

    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody advertUoReq: AdvertUoReq)


}