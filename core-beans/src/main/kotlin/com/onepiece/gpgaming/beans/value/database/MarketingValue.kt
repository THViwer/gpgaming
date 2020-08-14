package com.onepiece.gpgaming.beans.value.database

import com.fasterxml.jackson.annotation.JsonIgnore

class MarketingValue {

    data class MarketingCo(

            @JsonIgnore
            val clientId: Int,

            // 名称
            val name:  String,

            // 优惠Id
            val promotionId: Int,

            // 优惠码
            @JsonIgnore
            val promotionCode: String = "",

            // 消息模板
            val messageTemplate: String
    )

    data class MarketingUo(

            val id: Int,

            // 名称
            val name: String,

            // 优惠Id
            val promotionId: Int,

            // 优惠码
            @JsonIgnore
            val promotionCode: String = "",

            // 消息模板
            val messageTemplate: String
    )

    data class MarketVo(

            val id: Int,

            // 名称
            val name: String,

            // 优惠Id
            val promotionId: Int,

            // 优惠标题
//            val promotionTitle: String,

            // 优惠码
            val promotionCode: String,

            // 消息模板
            val messageTemplate: String,

            // 链接地址
            val links: List<String>
    )

    data class RegisterSmsTemplateReq(

            // 是否开启注册消息通知
            val enableRegisterMessage: Boolean,

            // 注册消息模板
            val registerMessageTemplate: String
    )

}