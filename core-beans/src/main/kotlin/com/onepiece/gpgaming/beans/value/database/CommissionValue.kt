package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal

sealed class CommissionValue {

    data class CommissionCo(

            // bossId
            val bossId: Int,

            // 存活人数
            val activeCount: Int,

            // 佣金比例
            val scale: BigDecimal,

            // 状态
            val status: Status
    )

    data class CommissionUo(

            val id: Int,

            // 存活人数
            val activeCount: Int,

            // 佣金比例
            val scale: BigDecimal,

            // 状态
            val status: Status
    )


}