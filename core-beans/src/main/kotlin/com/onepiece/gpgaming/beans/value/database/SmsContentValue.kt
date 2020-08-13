package com.onepiece.gpgaming.beans.value.database

sealed class SmsContentValue {

    data class SmsContentCo(

            // 层级 id
            val levelId: Int?,

            //  会员Id
            val memberIds: Int?,

            // 手机号(用，分割)
            val phones: String?,

            // 内容
            val content:  String

    )

}