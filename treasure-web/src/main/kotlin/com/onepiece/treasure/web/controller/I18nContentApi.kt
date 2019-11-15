package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.value.internet.web.I18nContentCoReq
import com.onepiece.treasure.beans.value.internet.web.I18nContentVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus


@Api(tags = ["web setting"], description = " ")
interface I18nContentApi {

    @ApiOperation(tags = ["web setting"], value = "支持语言列表")
    fun languages(): List<Language>

    @ApiOperation(tags = ["web setting"], value = "公告 -> 列表")
    fun all(): List<I18nContentVo>

    @ApiOperation(tags = ["web setting"], value = "国际化 -> 内容创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody i18nContentCoReq: I18nContentCoReq)

    @ApiOperation(tags = ["web setting"], value = "国际化 -> 内容更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@RequestBody i18nContentVo: I18nContentVo)

}