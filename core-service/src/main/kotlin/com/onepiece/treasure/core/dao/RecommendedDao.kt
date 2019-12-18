package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Recommended
import com.onepiece.treasure.beans.value.database.RecommendedValue
import com.onepiece.treasure.core.dao.basic.BasicDao

interface RecommendedDao: BasicDao<Recommended> {

    fun create(co: RecommendedValue.CreateVo): Boolean

    fun update(uo: RecommendedValue.UpdateVo): Boolean

}