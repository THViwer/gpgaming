package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.model.I18nContent

data class I18nContentCo(

        // 厅主Id
        val clientId: Int,

        // 语言
        val language: Language,

        // 配置Id
        val configId: Int,

        // 配置类型
        val configType: I18nConfig,

        // 内容
        val content: I18nContent.II18nContent
)


data class I18nContentUo(

        val id: Int,

        // 内容
        val content: I18nContent.II18nContent

)