package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.enums.Language
import java.time.LocalDateTime


/**
 * 国际化配置内容配置
 */
data class I18nContent (

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 标题
        val title: String,

        // 内容
        val content: String,

        // 简介
        val synopsis: String?,

        // 语言
        val language: Language,

        // 配置Id 当configType == Promotion时 才会有值
        val configId: Int?,

        // 配置类型
        val configType: I18nConfig,

        // 创建时间
        val createdTime: LocalDateTime

)
