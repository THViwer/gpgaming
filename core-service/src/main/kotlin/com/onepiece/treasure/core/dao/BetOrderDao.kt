package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.BetOrder
import com.onepiece.treasure.beans.value.database.BetOrderReport
import com.onepiece.treasure.beans.value.database.BetOrderValue
import com.onepiece.treasure.core.dao.basic.BasicDao
import java.time.LocalDate
import kotlin.math.absoluteValue

interface BetOrderDao: BasicDao<BetOrder> {

    fun getRuleTable(clientId: Int, memberId: Int, platform: Platform): String {
        val tableRule = this.getRuleKey(clientId, memberId, platform)
        return this.getRuleTable(tableRule)
    }

    fun getRuleKey(clientId: Int, memberId: Int, platform: Platform): String {
        return "$clientId$memberId$platform"
    }

    fun getRuleTable(tableRule: String): String {
        val index = tableRule.hashCode().absoluteValue % 8
        return "bet_order_$index"
    }

    fun batch(orders: List<BetOrderValue.BetOrderCo>)

    fun getBets(clientId: Int, memberId: Int, platform: Platform): List<BetOrder>

    fun getNotMarkBets(table: String, startId: Int): List<BetOrderValue.BetMarkVo>

    fun markBet(table: String, startId: Int, endId: Int): Boolean

    fun getLastNotMarkId(table: String): Int

    fun report(startDate: LocalDate, endDate: LocalDate): List<BetOrderReport>

}