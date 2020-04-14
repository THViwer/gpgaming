package com.onepiece.gpgaming.payment

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.model.pay.PayConfig
import java.math.BigDecimal

data class PayRequest(

        // 订单Id
        val orderId: String,

        // 金额
        val amount: BigDecimal,

        // 业主Id
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 用户名
        val username: String,

        // 用户选择的银行
        val selectBank: Bank?,

        // 支付类型
        val payType: PayType,

        // 支付配置
        val payConfig: PayConfig,

        // 支付成功跳转url
        val responseUrl: String,

        // 失败的跳转Url
        val failResponseUrl: String,

        // 语言
        val language: Language
)