package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Platform
import java.time.LocalDateTime

/**
 * 平台介绍
 */
data class PlatformIntroduction (
        // id
        val id: Int,

        // 平台
        val platform: Platform,

        // 默认内容配置Id
        val defaultContentId: Int,

        // 创建时间
        val createdTime: LocalDateTime
)