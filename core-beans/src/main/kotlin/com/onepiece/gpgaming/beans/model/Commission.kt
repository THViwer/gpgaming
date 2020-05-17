package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.CommissionType
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

        // 佣金设置类型
        val type: CommissionType,

        // 存活人数
        val activeCount: Int,

        // 最小充值金额
        val minDepositAmount: BigDecimal,

        // 佣金比例
        val scale: BigDecimal,

        // 状态
        val status: Status

)