package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 厅主余额表
 */
data class Balance (

        // id 主键
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 当前余额
        val balance: BigDecimal,

        // 总余额
        val totalBalance: BigDecimal,

        // 赠送金额
        val giftBalance: BigDecimal,

        // 创建时间
        val createdTime: LocalDateTime,

        val status: Status

)