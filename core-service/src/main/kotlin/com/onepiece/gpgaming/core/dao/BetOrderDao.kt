package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.BetOrder
import com.onepiece.gpgaming.beans.value.database.BetOrderReport
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import com.onepiece.gpgaming.beans.value.database.BetReportValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.math.absoluteValue

interface BetOrderDao: BasicDao<BetOrder> {

    fun getRuleTable(clientId: Int, memberId: Int): String {
        val tableRule = this.getRuleKey(clientId, memberId)
        return this.getRuleTable(tableRule)
    }

    fun getRuleKey(clientId: Int, memberId: Int): String {
        return "$clientId$memberId"
    }

    fun getRuleTable(tableRule: String): String {
        val index = tableRule.hashCode().absoluteValue % 8
        return "bet_order_$index"
    }

    fun batch(orders: List<BetOrderValue.BetOrderCo>)


    fun getBets(query: BetOrderValue.BetOrderQuery): List<BetOrder>

    fun getBets(clientId: Int, memberId: Int, platform: Platform): List<BetOrder>


    fun last500(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): List<BetOrder>

    fun getNotMarkBets(table: String, startId: Int): List<BetOrderValue.BetMarkVo>

    fun markBet(table: String, startId: Int, endId: Int): Boolean

    fun getLastNotMarkId(table: String): Int

    fun report(memberId: Int? = null, startDate: LocalDate, endDate: LocalDate): List<BetOrderReport>

    fun mreport(clientId: Int?, memberId: Int?, startDate: LocalDate): List<BetReportValue.MBetReport>

    fun creport(startDate: LocalDate): List<BetReportValue.CBetReport>

    fun getTotalBet(clientId: Int, memberId: Int, startDate: LocalDate): BigDecimal

    fun delOldBet(startDate: LocalDate)

}