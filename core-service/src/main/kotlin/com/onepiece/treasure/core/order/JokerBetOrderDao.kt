package com.onepiece.treasure.core.order

import com.onepiece.treasure.core.dao.basic.BasicDao
import java.time.LocalDate

interface JokerBetOrderDao: BasicDao<JokerBetOrder> {

    fun creates(orders: List<JokerBetOrder>)

    fun query(query: JokerBetOrderValue.Query): List<JokerBetOrder>

    fun report(startDate: LocalDate, endDate: LocalDate): List<JokerBetOrderValue.JokerReport>

}