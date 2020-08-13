package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status

data class Market (

        // id
        val id: Int,

        // 业主Id
        val clientId: Int,

        // 名称
        val name: String,

        // 优惠Id
        val promotionId: Int,

        // 优惠码
        val promotionCode: String,

        // 消息模板
        val messageTemplate: String,

        // 状态
        val status: Status
)