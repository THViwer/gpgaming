package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.model.Recommended
import com.onepiece.gpgaming.beans.value.database.RecommendedValue

interface RecommendedService {

    fun all(clientId: Int): List<Recommended>

    fun getByType(clientId: Int, type: RecommendedType): List<Recommended>

    fun create(co: RecommendedValue.CreateVo)

    fun update(uo: RecommendedValue.UpdateVo)

}