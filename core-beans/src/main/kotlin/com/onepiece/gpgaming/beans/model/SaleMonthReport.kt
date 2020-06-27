package com.onepiece.gpgaming.beans.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class SaleMonthReport(

        val id: Int,

        // bossId
        val bossId: Int,

        // clientId
        val clientId: Int,

        // 电销Id
        val saleId: Int,

        // 日期
        val day: LocalDate,

        // 电销用户名
        val saleUsername: String,

        // 自己的顾客佣金比例
        val ownCustomerScale: BigDecimal,

        // 自己的顾客佣金
        val ownCustomerFee: BigDecimal,

        // 系统佣金比例
        val systemCustomerScale: BigDecimal,

        // 系统的顾客佣金
        val systemCustomerFee: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime


)