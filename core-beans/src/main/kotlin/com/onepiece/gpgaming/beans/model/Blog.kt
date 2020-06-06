package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

data class Blog (

        // id
        val id: Int,

        // 标题
        val title: String,

        // 图片
        val headImg: String,

        // 排序
        val sort: Int,

        // 平台
        val platform: Platform,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)
