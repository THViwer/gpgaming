package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.BetOrder
import com.onepiece.gpgaming.beans.value.database.BetOrderReport
import com.onepiece.gpgaming.beans.value.database.BetOrderValue
import java.time.LocalDate

interface BetOrderService {

    fun batch(orders: List<BetOrderValue.BetOrderCo>)

    fun getBets(clientId: Int, memberId: Int, platform: Platform): List<BetOrder>

    fun last500(clientId: Int, memberId: Int): List<BetOrder>

    fun getNotMarkBets(tableSequence: Int): List<BetOrderValue.BetMarkVo>

    fun report(startDate: LocalDate, endDate: LocalDate): List<BetOrderReport>

}