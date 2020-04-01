package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.PayOrder
import com.onepiece.gpgaming.beans.value.database.PayOrderValue
import java.time.LocalDate
import java.time.LocalDateTime

interface PayOrderDao {

    fun total(query: PayOrderValue.PayOrderQuery): Int

    fun query(query: PayOrderValue.PayOrderQuery): List<PayOrder>

    fun find(orderId: String): PayOrder

    fun create(co: PayOrderValue.PayOrderCo): Boolean

    fun check(uo: PayOrderValue.ConstraintUo): Boolean

    fun successful(orderId: String, thirdOrderId: String): Boolean

    fun failed(orderId: String): Boolean

    fun close(closeTime: LocalDateTime)

    // 会员报表
    fun mReport(startDate: LocalDate): List<PayOrderValue.PayOrderMReport>

    // 业主平台报表 constraint: 是否强制入款
    fun cpReport(startDate: LocalDate, constraint: Boolean): List<PayOrderValue.PayOrderCPReport>

    // 业主报表 constraint: 是否强制入款
    fun cReport(startDate: LocalDate, constraint: Boolean): List<PayOrderValue.PayOrderCReport>

}