package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.HotGame
import com.onepiece.gpgaming.beans.value.database.HotGameValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface HotGameDao: BasicDao<HotGame> {

    fun create(co: HotGameValue.HotGameCo): Boolean

    fun update(uo: HotGameValue.HotGameUo): Boolean

}