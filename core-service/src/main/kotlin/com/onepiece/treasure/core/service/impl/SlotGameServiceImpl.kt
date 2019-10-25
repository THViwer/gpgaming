package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.SlotGame
import com.onepiece.treasure.beans.value.database.SlotGameCo
import com.onepiece.treasure.beans.value.database.SlotGameUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.SlotGameDao
import com.onepiece.treasure.core.service.SlotGameService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class SlotGameServiceImpl(
        private val slotGameDao: SlotGameDao,
        private val redisService: RedisService
) : SlotGameService {

    override fun findByPlatform(platform: Platform): List<SlotGame> {
        val redisKey = OnePieceRedisKeyConstant.slotGames(platform)
        return redisService.getList(redisKey, SlotGame::class.java) {
            slotGameDao.findByPlatform(platform)
        }
    }

    override fun create(slotGameCo: SlotGameCo) {
        val state = slotGameDao.create(slotGameCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.slotGames(slotGameCo.platform))
    }

    override fun update(slotGameUo: SlotGameUo) {
        val hasSlotGame = slotGameDao.get(slotGameUo.id)

        val state = slotGameDao.update(slotGameUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.slotGames(hasSlotGame.platform))
    }
}