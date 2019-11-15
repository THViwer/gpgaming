package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.database.PromotionCo
import com.onepiece.treasure.beans.value.database.PromotionUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface PromotionDao: BasicDao<Promotion> {

    fun create(promotionCo: PromotionCo): Int

    fun update(promotionUo: PromotionUo): Boolean

}