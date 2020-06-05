package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.ShowPosition

sealed class SeoValue {

    data class SeoUo(
            val clientId: Int,

            val title: String,

            val keywords: String,

            val description: String,

            // 在线聊天Id
            val liveChatId: String,

            // 是否打开新的窗口
            val liveChatTab: Boolean,

            // google统计Id
            val googleStatisticsId: String,

            //面子书广告
            val facebookTr: String,

            //facebook 显示位置
            val facebookShowPosition: ShowPosition,

            // asg content
            val asgContent: String

    )

    data class SeoVo(
            val title: String,

            val keywords: String,

            val description: String,

            // 在线聊天Id
            val liveChatId: String,

            // 是否打开新的窗口
            val liveChatTab: Boolean,

            // google统计Id
            val googleStatisticsId: String,

            //面子书广告
            val facebookTr: String,

            //facebook 显示位置
            val facebookShowPosition: ShowPosition,

            // asg content
            val asgContent: String
    )

}