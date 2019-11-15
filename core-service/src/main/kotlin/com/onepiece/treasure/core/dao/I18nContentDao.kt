package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.model.I18nContent
import com.onepiece.treasure.beans.value.database.I18nContentCo
import com.onepiece.treasure.beans.value.database.I18nContentUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface I18nContentDao : BasicDao<I18nContent> {

    fun create(i18nContentCo: I18nContentCo): Int

    fun update(i18nContentUo: I18nContentUo): Boolean

    fun getConfigType(clientId: Int, configType: I18nConfig): List<I18nContent>

}