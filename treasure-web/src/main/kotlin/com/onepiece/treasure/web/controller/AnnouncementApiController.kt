//package com.onepiece.treasure.web.controller
//
//import com.onepiece.treasure.beans.enums.I18nConfig
//import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
//import com.onepiece.treasure.beans.value.database.AnnouncementCo
//import com.onepiece.treasure.beans.value.internet.web.AnnouncementCoReq
//import com.onepiece.treasure.beans.value.internet.web.AnnouncementVo
//import com.onepiece.treasure.beans.value.internet.web.I18nContentVo
//import com.onepiece.treasure.core.service.AnnouncementService
//import com.onepiece.treasure.core.service.I18nContentService
//import com.onepiece.treasure.web.controller.basic.BasicController
//import org.springframework.web.bind.annotation.*
//
//@RestController
//@RequestMapping("/announcement")
//class AnnouncementApiController(
//        private val announcementService: AnnouncementService,
//        private val i18nContentService: I18nContentService
//) : BasicController(), AnnouncementApi {
//
//    @GetMapping
//    override fun all(): List<AnnouncementVo> {
//        val announcements = announcementService.all(clientId = clientId)
//        if (announcements.isEmpty()) return emptyList()
//
//        val i18nContentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Announcement).groupBy { it.configId }
//
//        return announcements.map {  announcement ->
//
//            val i18nContents = i18nContentMap[announcement.id] ?: error(OnePieceExceptionCode.DATA_FAIL)
//            val defaultContent = i18nContents.first()
//
//            AnnouncementVo(id = announcement.id, clientId = announcement.clientId, content = defaultContent.content, title = defaultContent.title, synopsis = defaultContent.synopsis,
//                    createdTime = announcement.createdTime, updatedTime = announcement.updatedTime, i18nContents = i18nContents)
//        }
//    }
//
//    @PostMapping
//    override fun create(@RequestBody announcementCoReq: AnnouncementCoReq) {
//        val announcementCo = AnnouncementCo(clientId = clientId, title = announcementCoReq.title, content = announcementCoReq.content, synopsis = announcementCoReq.synopsis,
//                language = announcementCoReq.language)
//        announcementService.create(announcementCo)
//
//    }
//
////    @PutMapping
////    override fun update(@RequestBody announcementUoReq: AnnouncementUoReq) {
////        val announcementUo = AnnouncementUo(id = announcementUoReq.id, title = announcementUoReq.title, content = announcementUoReq.content)
////        announcementService.update(announcementUo)
////    }
//}