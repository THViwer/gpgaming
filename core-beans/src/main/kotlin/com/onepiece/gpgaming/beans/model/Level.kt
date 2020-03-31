package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
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

        // 反水比例
        val backwater: BigDecimal,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)