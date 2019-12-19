package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.AppDown
import com.onepiece.gpgaming.beans.value.database.AppDownValue

interface AppDownService {

    fun all(): List<AppDown>

    fun create(appDown: AppDown)

    fun update(update: AppDownValue.Update)

}