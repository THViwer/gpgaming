package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.value.database.I18nContentCo
import com.onepiece.gpgaming.beans.value.database.I18nContentUo

interface I18nContentService {

    fun create(i18nContentCo: I18nContentCo): Int

    fun update(i18nContentUo: I18nContentUo)

    fun getConfigType(clientId: Int, configType: I18nConfig): List<I18nContent>

    fun getConfigs(clientId: Int): List<I18nContent>

//    fun getConfigType(clientId: Int, configId: String, configType: I18nConfig, language: Language): I18nContent
}