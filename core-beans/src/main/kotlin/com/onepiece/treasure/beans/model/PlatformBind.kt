package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

/**
 * 厅主开通平台表
 */
data class PlatformBind(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 状态
        val status: Status,

        // 开通时间
        val createdTime: LocalDateTime

)