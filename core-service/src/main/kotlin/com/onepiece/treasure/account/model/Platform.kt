package com.onepiece.treasure.account.model

import com.onepiece.treasure.account.model.enums.Status
import java.time.LocalDateTime

/**
 * 第三方平台
 */
data class Platform (

        // id
        val id: Int,

        // 平台名称
        val name: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime

)