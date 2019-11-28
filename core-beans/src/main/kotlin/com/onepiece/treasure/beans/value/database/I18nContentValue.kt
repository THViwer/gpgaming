package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.enums.Language

data class I18nContentCo(

        // 厅主Id
        val clientId: Int,

        // 标题
        val title: String,

        // banner
        val banner: String?,

        // 内容
        val content: String,

        // 简介
        val synopsis: String?,

        // 注意事项
        val precautions: String?,

        // 语言
        val language: Language,

        // 配置Id
        val configId: Int?,

        // 配置类型
        val configType: I18nConfig
    )


data class I18nContentUo(

        val id: Int,

        // 标题
        val title: String,

        // banner
        val banner: String?,

        // 内容
        val content: String,

        // 简介
        val synopsis: String?,

        // 注意事项
        val precautions: String?,

        // 语言
        val language: Language
)