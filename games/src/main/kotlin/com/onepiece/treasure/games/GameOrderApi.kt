package com.onepiece.treasure.games

import com.onepiece.treasure.core.order.BetOrderValue
import com.onepiece.treasure.games.value.ClientAuthVo
import java.time.LocalDate
import java.time.LocalDateTime

interface GameOrderApi {

    fun synOrder(clientAuthVo: ClientAuthVo?, startTime: LocalDateTime, endTime: LocalDateTime): String

    fun report(clientAuthVo: ClientAuthVo?, startDate: LocalDate, endDate: LocalDate): List<BetOrderValue.Report>

    fun query(clientAuthVo: ClientAuthVo?, query: BetOrderValue.Query): Any

}