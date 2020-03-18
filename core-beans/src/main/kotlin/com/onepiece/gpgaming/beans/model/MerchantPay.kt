package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.pay.PayConfig
import java.time.LocalDateTime

/**
 * Merchant   : merchant.m3pay.com
id   : T004
pwd   : 20200316_T004
Serial Key    : dbb06d88-38ae-4aa5-a49a-2f77ce31efd8
 */
data class MerchantPay (

        // id
        val id: Int,

        // 创建日期
        val clientId: Int,

        // 类型
        val type: PayType,

        // 支付配置
        val payConfig: PayConfig,

        // 状态
        val status: Status,

        // 创建日期
        val createdTime: LocalDateTime
)