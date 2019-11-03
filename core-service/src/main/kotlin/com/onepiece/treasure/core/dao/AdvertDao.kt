package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Advert
import com.onepiece.treasure.beans.value.database.AdvertCo
import com.onepiece.treasure.beans.value.database.AdvertUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface AdvertDao: BasicDao<Advert> {

    fun create(advertCo: AdvertCo): Boolean

    fun update(advertUo: AdvertUo): Boolean

}