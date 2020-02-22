package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.HotGameType
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

data class HotGame (

        // id
        val id: Int,

        // 厅主id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 游戏Id
        val gameId: String,

        // 热门游戏类型
        val type: HotGameType,

        // 状态
        val status: Status,

        // 创建日期
        val createdTime: LocalDateTime

)