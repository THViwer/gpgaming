package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.model.PayOrder
import com.onepiece.gpgaming.beans.value.database.PayOrderValue
import java.time.LocalDateTime

interface PayOrderService  {

    fun page(query: PayOrderValue.PayOrderQuery): Page<PayOrder>

    fun query(query: PayOrderValue.PayOrderQuery): List<PayOrder>

    fun create(co: PayOrderValue.PayOrderCo)

    fun check(uo: PayOrderValue.ConstraintUo)

    fun successful(orderId: String, thirdOrderId: String)

    fun close(closeTime: LocalDateTime)

}