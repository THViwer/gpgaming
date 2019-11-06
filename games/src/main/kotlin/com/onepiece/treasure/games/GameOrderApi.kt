package com.onepiece.treasure.games

import com.onepiece.treasure.core.order.BetOrderValue
import java.time.LocalDate
import java.time.LocalDateTime

interface GameOrderApi {

    fun synOrder(startTime: LocalDateTime, endTime: LocalDateTime): String

    fun report(startDate: LocalDate, endDate: LocalDate): List<BetOrderValue.Report>

    fun query(query: BetOrderValue.Query): Any

}