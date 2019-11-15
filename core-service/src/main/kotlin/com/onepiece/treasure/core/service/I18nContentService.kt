package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.value.database.I18nContentCo
import com.onepiece.treasure.beans.value.database.I18nContentUo
import com.onepiece.treasure.beans.value.internet.web.I18nContentVo

interface I18nContentService {

    fun create(i18nContentCo: I18nContentCo): Int

    fun update(i18nContentUo: I18nContentUo)

    fun getConfigType(clientId: Int, configType: I18nConfig): List<I18nContentVo>

}