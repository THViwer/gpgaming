package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.AppDown
import com.onepiece.treasure.beans.value.database.AppDownValue
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.AppDownDao
import com.onepiece.treasure.core.service.AppDownService
import com.onepiece.treasure.utils.RedisService
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