package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Recommended
import com.onepiece.gpgaming.beans.value.database.RecommendedValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface RecommendedDao: BasicDao<Recommended> {

    fun create(co: RecommendedValue.CreateVo): Boolean

    fun update(uo: RecommendedValue.UpdateVo): Boolean

}