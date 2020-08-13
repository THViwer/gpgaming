package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Status

data class Marketing (

        // id
        val id: Int,

        // 优惠Id
        val promotionId: Int,

        // 优惠码
        val promotionCode: String,

        // 消息模板
        val messageTemplate: String,

        // 状态
        val status: Status
)