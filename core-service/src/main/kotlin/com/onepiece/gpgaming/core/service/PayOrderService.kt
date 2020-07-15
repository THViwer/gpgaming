package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.model.PayOrder
import com.onepiece.gpgaming.beans.value.database.PayOrderValue
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

interface PayOrderService  {

    fun page(query: PayOrderValue.PayOrderQuery): Page<PayOrder>

    fun query(query: PayOrderValue.PayOrderQuery): List<PayOrder>

    fun summary(query: PayOrderValue.PayOrderQuery): List<PayOrderValue.ThirdPaySummary>

    fun create(co: PayOrderValue.PayOrderCo)

    fun check(uo: PayOrderValue.ConstraintUo)

    fun successful(orderId: String, thirdOrderId: String)

    fun failed(orderId: String)

    fun close(closeTime: LocalDateTime)

    fun sumSuccessful(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): BigDecimal

}