package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.HotGame
import com.onepiece.gpgaming.beans.value.database.HotGameValue
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.HotGameDao
import com.onepiece.gpgaming.core.service.HotGameService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service

@Service
class HotGameServiceImpl(
        private val redisService: RedisService,
        private val hotGameDao: HotGameDao
) : HotGameService {

    override fun all(clientId: Int): List<HotGame> {
        val redisKey = OnePieceRedisKeyConstant.getHotGames(clientId)
        return redisService.getList(redisKey, HotGame::class.java) {
            hotGameDao.all(clientId)
        }
    }

    override fun list(clientId: Int): List<HotGame> {
        return this.all(clientId).filter { it.status == Status.Normal }
    }

    override fun create(co: HotGameValue.HotGameCo) {

        val flag = hotGameDao.create(co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.getHotGames(co.clientId))

    }

    override fun update(uo: HotGameValue.HotGameUo) {
        val hotGame = hotGameDao.get(uo.id)

        val flag = hotGameDao.update(uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.getHotGames(hotGame.clientId))
    }
}