package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Bank
import com.onepiece.treasure.beans.enums.WithdrawState
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 充值订单
 */
data class Withdraw(

        val id: Int,

        // id
        val orderId: String,

        // 流程Id 用于乐观锁
        val processId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 会员银行卡Id
        val memberBankId: Int,

        // 取款银行
        val memberBank: Bank,

        // 会员银行卡号
        val memberBankCardNumber: String,

        // 会员姓名
        val memberName: String,

        // 提款金额
        val money: BigDecimal,

        // 取款状态
        val state: WithdrawState,

        // 备注
        val remarks: String?,

        // 锁定人Id
        val lockWaiterId: Int?,

        // 锁定人名称
        val lockWaiterName: String?,

        // 创建时间
        val createdTime: LocalDateTime,

        // 订单结束时间
        val endTime: LocalDateTime?
)