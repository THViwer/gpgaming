package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

data class AppDown(

        // id
        val id: Int,

        // 平台
        val platform: Platform,

        // ios下载地址
        val iosPath: String?,

        // android下载地址
        val androidPath: String?,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime

)
