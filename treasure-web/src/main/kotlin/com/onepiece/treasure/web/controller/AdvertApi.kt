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

    @ApiOperation(tags = ["index"], value = "首页设置 -> 列表")
    fun all(): List<AdvertVo>

    @ApiOperation(tags = ["index"], value = "首页设置 -> 创建")
    fun create(@RequestBody advertCoReq: AdvertCoReq)

    @ApiOperation(tags = ["index"], value = "首页设置 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody advertUoReq: AdvertUoReq)


}