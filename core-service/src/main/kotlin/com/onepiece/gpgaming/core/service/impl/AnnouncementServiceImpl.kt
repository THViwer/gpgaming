//package com.onepiece.treasure.core.service.impl
//
//import com.onepiece.treasure.beans.enums.I18nConfig
//import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
//import com.onepiece.treasure.beans.model.Announcement
//import com.onepiece.treasure.beans.value.database.AnnouncementCo
//import com.onepiece.treasure.beans.value.database.I18nContentCo
//import com.onepiece.treasure.core.dao.AnnouncementDao
//import com.onepiece.treasure.core.service.AnnouncementService
//import com.onepiece.treasure.core.service.I18nContentService
//import com.onepiece.treasure.utils.RedisService
//import org.springframework.stereotype.Service
//
//@Service
//class AnnouncementServiceImpl(
//        private val announcementDao: AnnouncementDao,
//        private val redisService: RedisService,
//        private val i18nContentService: I18nContentService
//) : AnnouncementService {
//
//    override fun last(clientId: Int): Announcement? {
//        return this.all(clientId).maxBy { it.id }
//
//    }
//
//    override fun all(clientId: Int): List<Announcement> {
//        return announcementDao.all(clientId)
//    }
//
//    override fun create(announcementCo: AnnouncementCo) {
//        val id = announcementDao.create(announcementCo)
//        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }
//
//        val i18nContentCo = I18nContentCo(clientId = announcementCo.clientId, title = announcementCo.title, synopsis = announcementCo.synopsis,
//                content = announcementCo.content, configId = id, configType = I18nConfig.Announcement, language = announcementCo.language)
//        i18nContentService.create(i18nContentCo)
//    }
//
////    override fun update(announcementUo: AnnouncementUo) {
////        val announcement = announcementDao.get(announcementUo.id)
////
////        val state = announcementDao.update(announcementUo)
////        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
////
////        redisService.delete(OnePieceRedisKeyConstant.lastAnnouncement(announcement.clientId))
////    }
//}