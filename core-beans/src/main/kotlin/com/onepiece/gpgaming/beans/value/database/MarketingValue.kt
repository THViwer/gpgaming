package com.onepiece.gpgaming.beans.value.database

class MarketingValue {

    data class MarketingCo(

            // 优惠Id
            val promotionId: Int,

            // 优惠码
            val promotionCode: String,

            // 消息模板
            val messageTemplate: String
    )

    data class MarketingUo(

            val id: Int,

            // 优惠Id
            val promotionId: Int,

            // 优惠码
            val promotionCode: String,

            // 消息模板
            val messageTemplate: String
    )

}