package com.onepiece.gpgaming.beans.value.internet.web

sealed class SeoValue {

    data class SeoUo(
            val clientId: Int,

            val title: String,

            val keywords: String,

            val description: String,

            // 在线聊天Id
            val liveChatId: String,

            // google统计Id
            val googleStatisticsId: String,

            //面子书广告
            val facebookTr: String

            )

    data class SeoVo(
            val title: String,

            val keywords: String,

            val description: String,

            // 在线聊天Id
            val liveChatId: String,

            // google统计Id
            val googleStatisticsId: String,

            //面子书广告
            val facebookTr: String
    )

}