package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.PlatformBind
import com.onepiece.gpgaming.beans.value.database.PlatformBindCo
import com.onepiece.gpgaming.beans.value.database.PlatformBindUo
import com.onepiece.gpgaming.core.IndexUtil
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.PlatformBindDao
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class PlatformBindServiceImpl(
        private val platformBindDao: PlatformBindDao,
        private val redisService: RedisService
) : PlatformBindService {

    @Autowired
    lateinit var indexUtil: IndexUtil


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
            platformBindDao.allWithDel(clientId)
        }
    }

    override fun create(platformBindCo: PlatformBindCo) {
        val state = platformBindDao.create(platformBindCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.openPlatforms(platformBindCo.clientId))

        indexUtil.generatorIndexPage(platformBindCo.clientId)
    }

    override fun update(platformBindUo: PlatformBindUo) {
        val bind = platformBindDao.get(platformBindUo.id)

        val state = platformBindDao.update(platformBindUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(
                OnePieceRedisKeyConstant.openPlatforms(bind.clientId),
                OnePieceRedisKeyConstant.openPlatform(bind.clientId, bind.platform)
        )
        indexUtil.generatorIndexPage(bind.clientId)
    }

    override fun updateEarnestBalance(clientId: Int, platform: Platform, earnestBalance: BigDecimal) {

        val bind = this.findClientPlatforms(clientId).find { it.platform == platform }
        checkNotNull(bind) { OnePieceExceptionCode.DATA_FAIL }

        val flag = platformBindDao.updateEarnestBalance(bind.id, earnestBalance, bind.processId)
        check(flag) { OnePieceExceptionCode.EARNESTBALANCE_OVER }


    }
}