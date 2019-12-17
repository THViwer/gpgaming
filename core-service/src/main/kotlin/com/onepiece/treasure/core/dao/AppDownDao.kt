package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.AppDown
import com.onepiece.treasure.beans.value.database.AppDownValue
import com.onepiece.treasure.core.dao.basic.BasicDao

interface AppDownDao: BasicDao<AppDown> {

    fun create(appDown: AppDown): Boolean

    fun update(update: AppDownValue.Update): Boolean

}