package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Level
import com.onepiece.gpgaming.beans.value.database.LevelValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface LevelDao: BasicDao<Level> {

    fun create(levelCo: LevelValue.LevelCo): Boolean

    fun update(levelUo: LevelValue.LevelUo): Boolean

}