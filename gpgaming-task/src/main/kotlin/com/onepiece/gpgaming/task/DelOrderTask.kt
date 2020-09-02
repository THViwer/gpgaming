package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.core.dao.BetOrderDao
import com.onepiece.gpgaming.core.dao.DepositDao
import com.onepiece.gpgaming.core.dao.PayOrderDao
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DelOrderTask(
        private val betOrderDao: BetOrderDao,
        private val depositDao: DepositDao,
        private val payOrderDao: PayOrderDao
) {

    // 删除下注订单
    @Scheduled(cron = "0 10 2 * * ?")
    fun delBetOrder() {
        val startDate = LocalDate.now().minusMonths(1)
        betOrderDao.delOldBet(startDate = startDate)
    }

    // 删除充值订单
    @Scheduled(cron = "0 20 2 * * ?")
    fun delPayOrder() {
        val startDate = LocalDate.now().minusWeeks(1)
        depositDao.delOldOrder(startDate = startDate)

        payOrderDao.delOldOrder(startDate = startDate)
    }

}