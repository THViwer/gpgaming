package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Level
import com.onepiece.gpgaming.beans.value.database.LevelValue

interface LevelService {

    fun getDefaultLevel(clientId: Int): Level

    fun all(clientId: Int): List<Level>

    fun create(levelCo: LevelValue.LevelCo)

    fun update(levelUo: LevelValue.LevelUo)

}