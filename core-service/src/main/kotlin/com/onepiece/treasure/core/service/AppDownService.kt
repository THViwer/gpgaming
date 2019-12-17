package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.AppDown
import com.onepiece.treasure.beans.value.database.AppDownValue

interface AppDownService {

    fun all(): List<AppDown>

    fun create(appDown: AppDown)

    fun update(update: AppDownValue.Update)

}