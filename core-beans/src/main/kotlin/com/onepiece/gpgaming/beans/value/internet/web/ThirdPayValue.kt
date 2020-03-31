package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.PayType
import java.time.LocalDateTime

sealed class ThirdPayValue {

    data class SupportPay(

            // 支付Id
            val payId: Int,

            // 支付平台
            val payType: PayType

    ) {

        // 支付logo
        val logo: String = payType.logo

        val greyLogo: String = payType.greyLogo
    }

    data class SelectPayResult(

            // 支付数据
            val data: Map<String, Any>
    )

    data class OrderVo(

            // 订单Id
            val orderId: String,

            // 充值平台
            val payType: PayType,

            // 支付张台
            val state: PayState,

            // 创建日期
            val createdTime: LocalDateTime

    )


}