package com.onepiece.treasure.account.model

import com.onepiece.treasure.account.model.enums.OrderState
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 充值订单
 */
data class BankWithdraw(

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

        // 充值状态
        val state: OrderState,

        // 备注
        val remarks: String?,

        // 创建时间
        val createdTime: LocalDateTime,

        // 充值成功时间
        val successTime: LocalDateTime?,

        // 订单关闭时间
        val closedTime: LocalDateTime?
)