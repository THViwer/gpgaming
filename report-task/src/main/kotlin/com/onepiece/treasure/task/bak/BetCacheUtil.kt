package com.onepiece.treasure.task.bak

import com.onepiece.treasure.beans.value.order.BetCacheVo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.PlatformMemberDao
import com.onepiece.treasure.utils.RedisService
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
open class BetCacheUtil(
        private val redisService: RedisService,
        private val platformMemberDao: PlatformMemberDao
) {

    @Async
    open fun handler(unionId: String) {
        val caches = redisService.getList(OnePieceRedisKeyConstant.betCache(unionId), BetCacheVo::class.java) { emptyList()}
        //TODO 处理打码量

        redisService.delete(OnePieceRedisKeyConstant.betCache(unionId))

        if (caches.isEmpty()) return

        platformMemberDao.batchBet(caches)
    }
}