package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.WalletEvent
import java.math.BigDecimal

data class WalletNoteCo(
        // 厅主Id
        val clientId: Int,

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
        val remarks: String
)

data class WalletNoteQuery(

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 事件
        val event: WalletEvent?,

        // 事件列表
        val events: List<WalletEvent>?,

        // 是否已包含优惠
        val onlyPromotion: Boolean,

        // 当前条数
        val current: Int,

        // 大小
        val size: Int
)
