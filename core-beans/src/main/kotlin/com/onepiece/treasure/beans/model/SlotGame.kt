package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

data class SlotGame(

        // id
        val id: Int,

        // 平台
        val platform: Platform,

        // 游戏Id
        val gameId: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)