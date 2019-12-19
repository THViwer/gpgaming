package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.WalletEvent
import java.math.BigDecimal
import java.time.LocalDateTime

data class WalletNote(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 客服id
        val waiterId: Int?,

        // 会员Id
        val memberId: Int,

        // 事件Id
        val eventId: String?,

        // 事件
        val event: WalletEvent,

        // 操作金额
        val money: BigDecimal,

        // 优惠金额
        val promotionMoney: BigDecimal?,

        // 备注
        val remarks: String,

        // 创建时间
        val createdTime: LocalDateTime
)