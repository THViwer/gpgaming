package com.onepiece.gpgaming.beans.value.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal

sealed class VipValue {

    data class VipCo(
            // 业主Id
            @JsonIgnore
            val clientId: Int,

            // 名称
            val name: String,

            // 层级id
            val levelId: Int,

            // logo
            val logo: String,

            // d = 天, w = 周, m = 月, y = 年 如：10d = 10天
            val days: String,

            // 充值金额
            val depositAmount: BigDecimal
    )

    data class VipUo(

            // id
            val id: Int,

            // 名称
            val name: String,

            // 层级id
            val levelId: Int,

            // logo
            val logo: String,

            // d = 天, w = 周, m = 月, y = 年 如：10d = 10天
            val days: String,

            // 充值金额
            val depositAmount: BigDecimal,

            // 状态
            val status: Status
    )

}