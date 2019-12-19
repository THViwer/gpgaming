package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status

data class SlotGameCo(

        // 平台
        val platform: Platform,

        // 游戏类目
        val category: GameCategory,

        // 游戏Id
        val gameId: String,

        // 是否热门游戏
        val hot: Boolean,

        // 是否新游戏
        val new: Boolean,

        // 游戏名称
        val name: String,

        // 游戏图标
        val icon: String

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

        // 游戏名称
        val name: String? = null,

        // 游戏图标
        val icon: String? = null,

        // 状态
        val status: Status? = null
)