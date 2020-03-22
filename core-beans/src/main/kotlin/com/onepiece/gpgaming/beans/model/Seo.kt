package com.onepiece.gpgaming.beans.model

import java.time.LocalDateTime

data class Seo (

        // id
        val id: Int,

        // 厅主
        val clientId: Int,

        // 关键字
        val keywords: String,

        // 描述
        val description: String,

        // 黄建时间
        val createdTime: LocalDateTime
)