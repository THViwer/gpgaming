package com.onepiece.treasure.core.dao.value

import com.onepiece.treasure.core.model.enums.Platform
import com.onepiece.treasure.core.model.enums.Status

data class SlotGameCo(

        // 平台
        val platform: Platform,

        // 游戏Id
        val gameId: String

        )


data class SlotGameUo(

        // id
        val id: Int,

        // 平台
        val platform: Platform,

        // 游戏Id
        val gameId: String,

        // 状态
        val status: Status
)