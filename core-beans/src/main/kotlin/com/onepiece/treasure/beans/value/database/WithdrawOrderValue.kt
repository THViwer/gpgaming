package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.WithdrawState
import java.math.BigDecimal
import java.time.LocalDateTime


data class WithdrawQuery(

        val clientId: Int,

        val startTime: LocalDateTime,

        val endTime: LocalDateTime,

        val orderId: String?,

        val memberId: Int?,

        val state: WithdrawState?
)

data class WithdrawOrderCo(

        // id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 会员银行卡Id
        val memberBankId: Int,

        // 提款金额
        val money: BigDecimal,

        // 备注
        val remarks: String?

)


data class WithdrawOrderUo(

        // id
        val orderId: String,

        // 流程Id 用于乐观锁
        val processId: String,

        // 取款状态
        val state: WithdrawState,

        // 备注
        val remarks: String?
)