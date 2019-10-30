package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Level
import com.onepiece.treasure.beans.value.database.LevelCo
import com.onepiece.treasure.beans.value.database.LevelUo

interface LevelService {

    fun getDefaultLevel(clientId: Int): Level

    fun all(clientId: Int): List<Level>

    fun create(levelCo: LevelCo)

    fun update(levelUo: LevelUo)

}