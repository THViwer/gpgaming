package com.onepiece.gpgaming.beans.value.database

sealed class SmsContentValue {

    data class SmsContentCo(

            val clientId: Int,

            // 层级 id
            val levelId: Int?,

            //  会员Id
            val memberIds: Int?,

            // 手机号(用，分割)
            val phones: String?,

            // 验证码
            val code: String?,

            // 是否成功
            val successful: Boolean,

            // 内容
            val content: String

    )

}