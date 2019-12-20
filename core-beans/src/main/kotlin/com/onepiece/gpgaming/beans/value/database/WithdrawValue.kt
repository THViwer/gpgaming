package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.WithdrawState
import java.math.BigDecimal
import java.time.LocalDateTime


data class WithdrawQuery(

        val clientId: Int,

        val startTime: LocalDateTime?,

        val endTime: LocalDateTime?,

        val orderId: String?,

        val lockWaiterId: Int?,

        val memberId: Int?,

        val state: WithdrawState?

)

data class WithdrawCo(

        // id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username: String,

        // 会员银行卡Id
        val memberBankId: Int,

        // 取款银行
        val memberBank: Bank,

        // 取款银行卡号
        val memberBankCardNumber: String,

        // 提款金额
        val money: BigDecimal,

        // 备注
        val remarks: String?

)

data class WithdrawLockUo(

        // 厅主Id
        val clientId: Int,

        // 订单Id
        val orderId: String,

        // 流程Id
        val processId: String,

        // 锁定人员
        val lockWaiterId: Int,

        // 锁定人名称
        val lockWaiterName: String
)

data class WithdrawUo(

        // id
        val orderId: String,

        val clientId: Int,

        val waiterId: Int,

        // 流程Id 用于乐观锁
        val processId: String,

        // 取款状态
        val state: WithdrawState,

        // 备注
        val remarks: String?
)