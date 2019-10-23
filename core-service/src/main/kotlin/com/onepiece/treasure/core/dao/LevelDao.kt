package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicQueryDao
import com.onepiece.treasure.core.dao.value.LevelCo
import com.onepiece.treasure.core.dao.value.LevelUo
import com.onepiece.treasure.core.model.Level

interface LevelDao: BasicQueryDao<Level> {

    fun create(levelCo: LevelCo): Boolean

    fun update(levelUo: LevelUo): Boolean

}