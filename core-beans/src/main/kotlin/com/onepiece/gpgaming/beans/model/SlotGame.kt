package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

data class SlotGame(

        // id
        val id: Int,

        // 平台
        val platform: Platform,

        // 游戏分类
        val category: GameCategory,

        // 是否热门
        val hot: Boolean,

        // 是否新游戏
        val new: Boolean,

        // 游戏Id
        val gameId: String,

        // 游戏名称
        val name: String,

        // 游戏图标
        val icon: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)