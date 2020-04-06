package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal

data class ClientBankCo(

        // 厅主名称
        val clientId: Int,

        // 银行
        val bank: Bank,

        // 银行卡号
        val bankCardNumber: String,

        // 银行名称
        val name: String,

        // 层级
        val levelId: Int?,

        // 最小转账金额
        val minAmount: BigDecimal,

        // 最大转账金额
        val maxAmount: BigDecimal
)

data class ClientBankUo(

        // id
        val id: Int,

        // 银行
        val bank: Bank? = null,

        // 银行卡号
        val bankCardNumber: String? = null,

        // 姓名
        val name: String? = null,

        // 状态
        val status: Status? = null,

        // 层级
        val levelId: Int? = null,

        // 最小转账金额
        val minAmount: BigDecimal? = null,

        // 最大转账金额
        val maxAmount: BigDecimal? = null

)