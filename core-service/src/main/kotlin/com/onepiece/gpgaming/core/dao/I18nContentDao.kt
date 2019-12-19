package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.value.database.I18nContentCo
import com.onepiece.gpgaming.beans.value.database.I18nContentUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface I18nContentDao : BasicDao<I18nContent> {

    fun create(i18nContentCo: I18nContentCo): Int

    fun update(i18nContentUo: I18nContentUo): Boolean

    fun getConfigType(clientId: Int, configType: I18nConfig): List<I18nContent>

}