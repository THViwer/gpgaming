//package com.onepiece.treasure.web.controller
//
//import com.onepiece.treasure.beans.value.internet.web.WebSiteCoReq
//import com.onepiece.treasure.beans.value.internet.web.WebSiteUoReq
//import com.onepiece.treasure.beans.value.internet.web.WebSiteVo
//import io.swagger.annotations.Api
//import io.swagger.annotations.ApiOperation
//import org.springframework.http.HttpStatus
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.ResponseStatus
//
//@Api(tags = ["setting"], description = " ")
//interface WebSiteApi {
//
//    @ApiOperation(tags = ["setting"], value = "域名 -> 列表")
//    fun all(): List<WebSiteVo>
//
//    @ApiOperation(tags = ["setting"], value = "域名 -> 创建")
//    fun create(@RequestBody webSiteCoReq: WebSiteCoReq)
//
//    @ApiOperation(tags = ["setting"], value = "域名 -> 更新")
//    @ResponseStatus(code = HttpStatus.NO_CONTENT)
//    fun update(@RequestBody webSiteUoReq: WebSiteUoReq)
//
//}