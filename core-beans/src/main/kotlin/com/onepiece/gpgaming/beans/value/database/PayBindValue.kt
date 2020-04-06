package com.onepiece.gpgaming.beans.value.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal

sealed class PayBindValue {

    data class PayBindCo(

            // 业主Id
            @JsonIgnore
            val clientId: Int = 0,

            // 层级Id
            val levelId: Int?,

            // 支付平台
            val payType: PayType,

            // 支付配置
            val configJson: String,

            // 状态
            val status: Status,

            // 最小充值金额
            val minAmount: BigDecimal,

            // 最大充值金额
            val maxAmount: BigDecimal

    )

    data class PayBindUo(

            // id
            val id: Int,

            // 业主Id
            @JsonIgnore
            val clientId: Int,

            // 层级Id
            val levelId: Int?,

            // 支付配置
            val configJson: String?,

            // 状态
            val status: Status?,

            // 最小充值金额
            val minAmount: BigDecimal,

            // 最大充值金额
            val maxAmount: BigDecimal
    )



}