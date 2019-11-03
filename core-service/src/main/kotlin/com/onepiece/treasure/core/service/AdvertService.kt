package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Advert
import com.onepiece.treasure.beans.value.database.AdvertCo
import com.onepiece.treasure.beans.value.database.AdvertUo

interface AdvertService {

    fun all(clientId: Int): List<Advert>

    fun create(advertCo: AdvertCo)

    fun update(advertUo: AdvertUo)

}