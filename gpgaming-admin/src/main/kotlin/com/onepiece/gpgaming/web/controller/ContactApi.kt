package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.model.Contact
import com.onepiece.gpgaming.beans.value.internet.web.ContactValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus


@Api(tags = ["web setting"], description = "网站设置")
interface ContactApi {

    @ApiOperation(tags = ["web setting"], value = "联系我们 -> 列表")
    fun all(): List<Contact>

    @ApiOperation(tags = ["web setting"], value = "联系我们 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody create: ContactValue.Create)

    @ApiOperation(tags = ["web setting"], value = "联系我们 -> 更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody update: ContactValue.Update)

}