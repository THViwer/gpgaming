package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.ShowPosition
import java.time.LocalDateTime

data class ClientConfig(

        // id
        val id: Int,

        // 厅主
        val clientId: Int,

        // 标题
        val title: String,

        // 关键字
        val keywords: String,

        // 描述
        val description: String,

        // 在线聊天Id
        val liveChatId: String,

        // 是否打开新的页面
        val liveChatTab: Boolean,

        // google统计Id
        val googleStatisticsId: String,

        //面子书广告
        val facebookTr: String,

        // facebook显示位置
        val facebookShowPosition: ShowPosition,

        // asg 广告内容
        val asgContent: String,

        // 短信注册模板
        val registerMessageTemplate:  String,

        // 创建时间
        val createdTime: LocalDateTime
)