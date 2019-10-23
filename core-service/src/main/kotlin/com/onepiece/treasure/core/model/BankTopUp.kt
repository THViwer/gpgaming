package com.onepiece.treasure.core.model

import com.onepiece.treasure.core.model.enums.Banks
import com.onepiece.treasure.core.model.enums.OrderState
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 充值订单
 */
data class BankTopUp(

        // id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 充值银行
        val bank: Banks,

        // 充值金额
        val money: BigDecimal,

        // 上传图片地址
        val imgPath: String,

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