package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Level
import com.onepiece.gpgaming.beans.value.database.LevelCo
import com.onepiece.gpgaming.beans.value.database.LevelUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface LevelDao: BasicDao<Level> {

    fun create(levelCo: LevelCo): Boolean

    fun update(levelUo: LevelUo): Boolean

}