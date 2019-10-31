package com.onepiece.treasure.beans.value.database

data class AnnouncementCo(

        // 厅主Id
        val clientId: Int,

        // 标题
        val title: String,

        // 内容
        val content: String
)

data class AnnouncementUo(

        // Id
        val id: Int,

        // 标题
        val title: String,

        // 内容
        val content: String

)