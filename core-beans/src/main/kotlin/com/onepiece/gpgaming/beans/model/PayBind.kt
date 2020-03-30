package com.onepiece.gpgaming.beans.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.pay.M3PayConfig
import com.onepiece.gpgaming.beans.model.pay.PayConfig
import java.time.LocalDateTime

data class PayBind (

        // id
        val id: Int,

        // 业主Id
        val clientId: Int,

        // 层级Id 如果为Null 则是全部
        val levelId: Int?,

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
        }
    }



}