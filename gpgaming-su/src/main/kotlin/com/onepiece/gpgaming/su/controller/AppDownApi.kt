package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.model.AppDown
import com.onepiece.gpgaming.beans.value.database.AppDownValue
import com.onepiece.gpgaming.beans.value.internet.web.AppDownWebValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["app"], description = " ")
interface AppDownApi {

    @ApiOperation(tags = ["app"], value = "列表")
    fun list(): List<AppDown>

    @ApiOperation(tags = ["app"], value = "创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody coReq: AppDownWebValue.CoReq)

    @ApiOperation(tags = ["app"], value = "更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody update: AppDownValue.Update)

}