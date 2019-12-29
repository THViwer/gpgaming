package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.GamePlatform
import com.onepiece.gpgaming.beans.value.database.GamePlatformValue
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.GamePlatformDao
import com.onepiece.gpgaming.core.service.GamePlatformService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service

@Service
class GamePlatformServiceImpl(
        private val gamePlatformDao: GamePlatformDao,
        private val redisService: RedisService
) : GamePlatformService {

    override fun all(): List<GamePlatform> {
        val redisKey = OnePieceRedisKeyConstant.getPlatforms()
        return redisService.getList(key = redisKey, clz = GamePlatform::class.java) {
            gamePlatformDao.all()
        }
    }

    override fun create(gamePlatformCo: GamePlatformValue.GamePlatformCo) {
        val state = gamePlatformDao.create(gamePlatformCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(gamePlatformUo: GamePlatformValue.GamePlatformUo) {
        val state = gamePlatformDao.update(gamePlatformUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}