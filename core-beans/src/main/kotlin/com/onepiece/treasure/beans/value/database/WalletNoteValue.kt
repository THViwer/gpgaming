package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.WalletEvent
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

        // 备注
        val remarks: String
)

data class WalletNoteQuery(

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 事件
        val event: WalletEvent?
)
