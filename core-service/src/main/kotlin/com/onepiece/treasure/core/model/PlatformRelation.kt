package com.onepiece.treasure.core.model

import com.onepiece.treasure.core.model.enums.Status
import java.time.LocalDateTime

/**
 * 厅主开通平台表
 */
data class PlatformRelation(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 平台Id
        val platformId: Int,

        // 状态
        val status: Status,

        // 开通时间
        val openTime: LocalDateTime

)