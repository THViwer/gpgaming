package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 厅主银行卡信息
 */
data class ClientBank(

        // id
        val id: Int,

        // 厅主名称
        val clientId: Int,

        // 银行
        val bank: Bank,

        // 银行卡号
        val bankCardNumber: String,

        // 层级Id
        val levelId: Int?,

        // 最小转账金额
        val minAmount: BigDecimal,

        // 最大转账金额
        val maxAmount: BigDecimal,

        // 银行名称
        val name: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)