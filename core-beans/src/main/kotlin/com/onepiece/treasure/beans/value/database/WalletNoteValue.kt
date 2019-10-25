package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.WalletEvent

data class WalletNoteCo(
        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 事件
        val event: WalletEvent,

        // 备注
        val remarks: String
)

data class WalletNoteQuery(

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 事件
        val event: WalletEvent
)
