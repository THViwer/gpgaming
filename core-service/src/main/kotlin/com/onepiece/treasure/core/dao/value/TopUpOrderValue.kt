package com.onepiece.treasure.core.dao.value

import com.onepiece.treasure.core.model.enums.Banks
import com.onepiece.treasure.core.model.enums.OrderState
import com.onepiece.treasure.core.model.enums.TopUpState
import java.math.BigDecimal
import java.time.LocalDateTime

data class TopUpOrderQuery(

        val clientId: Int,

        val startTime: LocalDateTime,

        val endTime: LocalDateTime,

        val orderId: String?,

        val memberId: Int?,

        val state: TopUpState?
)

data class TopUpOrderCo(

        // 订单Id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 充值银行
        val bank: Banks,

        // 银行卡号
        val bankCardNumber: String,

        // 充值金额
        val money: BigDecimal,

        // 上传图片地址
        val imgPath: String
)


data class TopUpOrderUo(

        // 订单Id
        val orderId: String,

        // 流程Id 用于乐观锁
        val processId: String,

        // 充值状态
        val state: OrderState,

        // 备注
        val remarks: String?
)