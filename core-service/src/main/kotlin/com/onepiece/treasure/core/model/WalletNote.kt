package com.onepiece.treasure.core.model

import com.onepiece.treasure.core.model.enums.WalletEvent
import java.time.LocalDateTime

data class WalletNote(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 事件
        val event: WalletEvent,

        // 备注
        val remark: String,

        // 创建时间
        val createdTime: LocalDateTime
)