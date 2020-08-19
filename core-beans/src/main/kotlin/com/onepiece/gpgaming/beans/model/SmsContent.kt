package com.onepiece.gpgaming.beans.model

import java.time.LocalDateTime

/**
 * 短信内容
 */
class SmsContent (

        val id:  Int,

        val clientId:  Int,

        // 层级 id
        val levelId: Int?,

        //  会员Id
        val memberIds: String?,

        // 手机号(用，分割)
        val phones: String?,

        // 内容
        val content:  String,

        // 创建时间
        val createdTime:  LocalDateTime

)