package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.database.PromotionCo
import com.onepiece.treasure.beans.value.database.PromotionUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface PromotionDao: BasicDao<Promotion> {

    fun create(promotionCo: PromotionCo): Int

    fun update(promotionUo: PromotionUo): Boolean

    fun find(clientId: Int, platform: Platform): List<Promotion>

}