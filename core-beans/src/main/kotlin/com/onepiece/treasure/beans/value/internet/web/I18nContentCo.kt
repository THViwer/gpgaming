package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.enums.Language

data class I18nContentCoReq(

        // 标题
        val title: String,

        // 内容
        val content: String,

        // 简介
        val synopsis: String?,

        // 语言
        val language: Language,

        // 配置Id
        val configId: Int,

        // 配置类型
        val configType: I18nConfig
    )
