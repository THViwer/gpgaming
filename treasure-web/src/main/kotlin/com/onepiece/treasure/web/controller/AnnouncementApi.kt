//package com.onepiece.treasure.web.controller
//
//import com.onepiece.treasure.beans.value.internet.web.AnnouncementCoReq
//import com.onepiece.treasure.beans.value.internet.web.AnnouncementVo
//import io.swagger.annotations.Api
//import io.swagger.annotations.ApiOperation
//import org.springframework.web.bind.annotation.RequestBody
//
//@Api(tags = ["index"], description = " ")
//interface AnnouncementApi  {
//
//    @ApiOperation(tags = ["index"], value = "公告 -> 列表")
//    fun all(): List<AnnouncementVo>
//
//    @ApiOperation(tags = ["index"], value = "公告 -> 创建")
//    fun create(@RequestBody announcementCoReq: AnnouncementCoReq)
//
////    @ApiOperation(tags = ["index"], value = "公告 -> 更新")
////    @ResponseStatus(HttpStatus.NO_CONTENT)
////    fun update(@RequestBody announcementUoReq: AnnouncementUoReq)
////
//
//}