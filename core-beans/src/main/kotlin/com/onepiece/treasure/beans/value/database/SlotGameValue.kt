package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.GameCategory
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status

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
        val platform: Platform? = null,

        // 游戏类型
        val category: GameCategory? = null,

        // 是否热门游戏
        val hot: Boolean? = null,

        // 是否新游戏
        val new: Boolean? = null,

        // 游戏Id
        val gameId: String? = null,

        // 状态
        val status: Status? = null
)