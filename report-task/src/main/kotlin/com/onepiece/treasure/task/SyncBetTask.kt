package com.onepiece.treasure.task

import com.onepiece.treasure.beans.value.order.BetCacheVo
import com.onepiece.treasure.core.service.BetOrderService
import com.onepiece.treasure.core.service.PlatformMemberService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean

@Component
class SyncBetTask(
        private val betOrderService: BetOrderService,
        private val platformMemberService: PlatformMemberService
) {

    private val running = AtomicBoolean(false)

    @Scheduled(cron="0/30 * *  * * ? ")
    fun start() {

        if (!running.getAndSet(true)) return

        (0 until 8).forEach { index ->

            val betMarks  = betOrderService.getNotMarkBets(index)
            if (betMarks.isEmpty()) return@forEach

            val data = betMarks.map {
                BetCacheVo(memberId = it.memberId, platform = it.platform, bet = it.betAmount, win = it.winAmount)
            }
            platformMemberService.batchBet(data)
        }

        running.set(false)
    }


}