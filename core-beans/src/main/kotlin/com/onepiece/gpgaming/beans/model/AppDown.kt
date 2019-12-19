package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
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
