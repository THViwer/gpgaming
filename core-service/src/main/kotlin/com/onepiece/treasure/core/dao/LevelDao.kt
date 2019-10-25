package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.LevelCo
import com.onepiece.treasure.beans.value.database.LevelUo
import com.onepiece.treasure.beans.model.Level

interface LevelDao: BasicDao<Level> {

    fun create(levelCo: LevelCo): Boolean

    fun update(levelUo: LevelUo): Boolean

}