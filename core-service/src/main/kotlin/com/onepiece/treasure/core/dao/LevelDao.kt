package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.LevelCo
import com.onepiece.treasure.core.dao.value.LevelUo
import com.onepiece.treasure.core.model.Level

interface LevelDao: BasicDao<Level> {

    fun create(levelCo: LevelCo): Boolean

    fun update(levelUo: LevelUo): Boolean

}