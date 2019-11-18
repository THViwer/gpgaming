package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.PlatformBind
import com.onepiece.treasure.beans.value.database.PlatformBindCo
import com.onepiece.treasure.beans.value.database.PlatformBindUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.PlatformBindDao
import com.onepiece.treasure.core.service.PlatformBindService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PlatformBindServiceImpl(
        private val platformBindDao: PlatformBindDao,
        private val redisService: RedisService
) : PlatformBindService {


    override fun all(): List<PlatformBind> {
        return platformBindDao.all()
    }

    override fun find(platform: Platform): List<PlatformBind> {
        return platformBindDao.find(platform)
    }

    override fun find(clientId: Int, platform: Platform): PlatformBind {

        val redisKey = OnePieceRedisKeyConstant.openPlatform(clientId, platform)
        return redisService.get(redisKey, PlatformBind::class.java) {
            this.findClientPlatforms(clientId).find { it.platform == platform }
        }!!
    }

    override fun findClientPlatforms(clientId: Int): List<PlatformBind> {
        val redisKey = OnePieceRedisKeyConstant.openPlatforms(clientId)

        return redisService.getList(redisKey, PlatformBind::class.java) {
            platformBindDao.all(clientId)
        }
    }

    override fun create(platformBindCo: PlatformBindCo) {
        val state = platformBindDao.create(platformBindCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.openPlatforms(platformBindCo.clientId))
    }

    override fun update(platformBindUo: PlatformBindUo) {
        val bind = platformBindDao.get(platformBindUo.id)

        val state = platformBindDao.update(platformBindUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(
                OnePieceRedisKeyConstant.openPlatforms(bind.clientId),
                OnePieceRedisKeyConstant.openPlatform(bind.clientId, bind.platform)
        )
    }

    override fun updateEarnestBalance(clientId: Int, platform: Platform, earnestBalance: BigDecimal) {

        val bind = this.findClientPlatforms(clientId).find { it.platform == platform }
        checkNotNull(bind) { OnePieceExceptionCode.DATA_FAIL }

        platformBindDao.updateEarnestBalance(bind.id, earnestBalance, bind.processId)

    }
}