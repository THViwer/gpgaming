package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.TransferState
import java.math.BigDecimal
import java.time.LocalDate

data class TransferOrderCo(

        // 订单Id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username: String,

        // 转账金额
        val money: BigDecimal,

        // 赠送金额
        val promotionAmount: BigDecimal,

        // 参加优惠活动Id
        val joinPromotionId: Int?,

        // 优惠信息
        val promotionJson: String?,

        // 转出平台
        val from: Platform,

        // 转入平台
        val to: Platform
)

data class TransferOrderUo(
        val orderId: String,

        val state: TransferState?,

        val transferOutAmount: BigDecimal?
)

data class TransferOrderReportQuery(

        val startDate: LocalDate,

        val endDate: LocalDate
)
