package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal

/**
 * 佣金比例
 */
data class Commission(

        // id
        val id: Int,

        // bossId
        val bossId: Int,

        // 存活人数
        val activeCount: Int,

        // 佣金比例
        val scale: BigDecimal,

        // 状态
        val status: Status

)