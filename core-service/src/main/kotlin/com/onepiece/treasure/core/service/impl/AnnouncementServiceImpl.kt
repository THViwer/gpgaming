package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Announcement
import com.onepiece.treasure.beans.value.database.AnnouncementCo
import com.onepiece.treasure.beans.value.database.AnnouncementUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.AnnouncementDao
import com.onepiece.treasure.core.service.AnnouncementService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class AnnouncementServiceImpl(
        private val announcementDao: AnnouncementDao,
        private val redisService: RedisService
) : AnnouncementService {

    override fun last(clientId: Int): Announcement? {
        val redisKey = OnePieceRedisKeyConstant.lastAnnouncement(clientId)
        return redisService.get(redisKey, Announcement::class.java) {
            this.all(clientId).maxBy { it.id }
        }
    }

    override fun all(clientId: Int): List<Announcement> {
        return announcementDao.all(clientId)

    }

    override fun create(announcementCo: AnnouncementCo) {
        val state = announcementDao.create(announcementCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(announcementUo: AnnouncementUo) {
        val announcement = announcementDao.get(announcementUo.id)

        val state = announcementDao.update(announcementUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.lastAnnouncement(announcement.clientId))
    }
}