package com.onepiece.treasure.core.model

import com.onepiece.treasure.core.model.enums.Status
import java.time.LocalDateTime

/**
 * 会员等级
 */
data class Level(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 名称
        val name: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)