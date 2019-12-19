package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Level
import com.onepiece.gpgaming.beans.value.database.LevelCo
import com.onepiece.gpgaming.beans.value.database.LevelUo
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.LevelDao
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.stereotype.Service

@Service
class LevelServiceImpl(
        private val levelDao: LevelDao,
        private val redisService: RedisService
): LevelService {

    override fun getDefaultLevel(clientId: Int): Level {
        return all(clientId).first { it.name == "default" }
    }

    override fun all(clientId: Int): List<Level> {
        val redisKey = OnePieceRedisKeyConstant.level(clientId)
        return redisService.getList(redisKey, Level::class.java) {
            levelDao.all(clientId)
        }
    }

    override fun create(levelCo: LevelCo) {
        val state = levelDao.create(levelCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.level(levelCo.clientId))
    }

    override fun update(levelUo: LevelUo) {

        val level = levelDao.get(levelUo.id)
        check(level.name != "default") { OnePieceExceptionCode.DEFAULT_LEVEL_FAIL }

        if (levelUo.status == Status.Stop) {
            //TODO 检察层级下是否还有人
        }

        val state = levelDao.update(levelUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.level(level.clientId))
    }
}