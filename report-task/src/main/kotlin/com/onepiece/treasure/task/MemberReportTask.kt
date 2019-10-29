package com.onepiece.treasure.task

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.MemberReport
import com.onepiece.treasure.core.dao.MemberReportDao
import com.onepiece.treasure.core.order.JokerBetOrderDao
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class MemberReportTask(
        private val jokerOrderDao: JokerBetOrderDao,
        private val memberReportDao: MemberReportDao
) {

    fun start() {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(1)
        val reports = jokerOrderDao.report(startDate = startDate, endDate = endDate)

        val now = LocalDateTime.now()
        val data = reports.map {
            MemberReport(id = -1, day = startDate, clientId = it.clientId, memberId = it.memberId, platform = Platform.Joker,
                    bet = it.amount, money = it.result, createdTime = now)
        }
        memberReportDao.creates(data)

    }

}