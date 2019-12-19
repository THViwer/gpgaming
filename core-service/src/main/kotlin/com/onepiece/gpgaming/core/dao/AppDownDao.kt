package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.AppDown
import com.onepiece.gpgaming.beans.value.database.AppDownValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface AppDownDao: BasicDao<AppDown> {

    fun create(appDown: AppDown): Boolean

    fun update(update: AppDownValue.Update): Boolean

}