package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.AppDown
import com.onepiece.gpgaming.beans.value.database.AppDownValue
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.AppDownDao
import com.onepiece.gpgaming.core.service.AppDownService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service

@Service
class AppDownServiceImpl(
        private val appDownDao: AppDownDao,
        private val redisService: RedisService
) : AppDownService {


    override fun all(): List<AppDown> {
        val redisKey = OnePieceRedisKeyConstant.getAllAppDown()
        return redisService.getList(key = redisKey, clz = AppDown::class.java) {
            appDownDao.all()
        }
    }

    override fun create(appDown: AppDown) {

        val state = appDownDao.create(appDown)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.getAllAppDown())

    }

    override fun update(update: AppDownValue.Update) {

        val state = appDownDao.update(update)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.getAllAppDown())

    }
}