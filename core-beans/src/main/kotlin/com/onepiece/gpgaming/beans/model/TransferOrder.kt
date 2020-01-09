package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.enums.TransferState
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransferOrder(

        // 订单Id
        val orderId: String,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 会员名
        val username: String,

        // 转账金额
        val money: BigDecimal,

        // 优惠活动金额
        val promotionAmount: BigDecimal,

        // 参加活动Id
        val joinPromotionId: Int?,

        // 优惠信息
        val promotionJson: String?,

        // 转出平台
        val from: Platform,

        // 转入平台
        val to: Platform,

        // 状态
        val state: TransferState,

        // 创建日期
        val createdTime: LocalDateTime,

        // 更新日期
        val updatedTime: LocalDateTime,

        // 状态
        val status: Status
)