package com.onepiece.gpgaming.beans.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.pay.M3PayConfig
import com.onepiece.gpgaming.beans.model.pay.PayConfig
import com.onepiece.gpgaming.beans.model.pay.SurePayConfig
import java.math.BigDecimal
import java.time.LocalDateTime

data class PayBind (

        // id
        val id: Int,

        // 业主Id
        val clientId: Int,

        // 层级Id 如果为Null 则是全部
        val levelId: Int?,

        // 最小充值金额
        val minAmount: BigDecimal,

        // 最大充值金额
        val maxAmount: BigDecimal,

        // 支付平台
        val payType: PayType,

        // 支付配置
        val configJson: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime

) {

    fun getConfig(objectMapper: ObjectMapper): PayConfig {
        return when (payType) {
            PayType.M3Pay -> objectMapper.readValue<M3PayConfig>(configJson)
            PayType.SurePay -> objectMapper.readValue<SurePayConfig>(configJson)
        }
    }



}