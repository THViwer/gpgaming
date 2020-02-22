package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.HotGame
import com.onepiece.gpgaming.beans.value.database.HotGameValue

interface HotGameService {

    fun all(clientId: Int): List<HotGame>

    fun list(clientId: Int): List<HotGame>

    fun create(co: HotGameValue.HotGameCo)

    fun update(uo: HotGameValue.HotGameUo)



}