package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.Level
import com.onepiece.gpgaming.beans.value.database.LevelCo
import com.onepiece.gpgaming.beans.value.database.LevelUo

interface LevelService {

    fun getDefaultLevel(clientId: Int): Level

    fun all(clientId: Int): List<Level>

    fun create(levelCo: LevelCo)

    fun update(levelUo: LevelUo)

}