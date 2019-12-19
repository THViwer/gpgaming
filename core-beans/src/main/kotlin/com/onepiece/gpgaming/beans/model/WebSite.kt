package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

data class WebSite(

        // id
        val id: Int,

        // 厅主
        val clientId: Int,

        // 域名
        val domain: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)