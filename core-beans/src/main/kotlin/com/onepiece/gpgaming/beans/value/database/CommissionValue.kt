package com.onepiece.gpgaming.beans.value.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.CommissionType
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal

sealed class CommissionValue {

    data class CommissionCo(

            // bossId
            @JsonIgnore
            val bossId: Int,

            // 存活人数
            val activeCount: Int,

            // 最小充值金额
            val minDepositAmount: BigDecimal,

            // 佣金类型
            val type: CommissionType,

            // 佣金比例
            val scale: BigDecimal,

            // 状态
            val status: Status

    )

    data class CommissionUo(

            val id: Int,

            // 存活人数
            val activeCount: Int,

            // 最小充值金额
            val minDepositAmount: BigDecimal,

            // 佣金比例
            val scale: BigDecimal,

            // 状态
            val status: Status
    )


}