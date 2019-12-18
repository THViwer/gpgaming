package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.enums.RecommendedType
import com.onepiece.treasure.beans.model.Recommended
import com.onepiece.treasure.beans.value.database.RecommendedValue

interface RecommendedService {

    fun all(clientId: Int): List<Recommended>

    fun getByType(clientId: Int, type: RecommendedType): List<Recommended>

    fun create(co: RecommendedValue.CreateVo)

    fun update(uo: RecommendedValue.UpdateVo)

}