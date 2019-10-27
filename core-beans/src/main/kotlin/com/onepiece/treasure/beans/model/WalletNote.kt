package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.WalletEvent
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

        // 备注
        val remarks: String,

        // 创建时间
        val createdTime: LocalDateTime
)