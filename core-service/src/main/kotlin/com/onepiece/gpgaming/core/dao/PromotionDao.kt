package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Promotion
import com.onepiece.gpgaming.beans.value.database.PromotionCo
import com.onepiece.gpgaming.beans.value.database.PromotionUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface PromotionDao: BasicDao<Promotion> {

    fun create(promotionCo: PromotionCo): Int

    fun update(promotionUo: PromotionUo): Boolean

//    fun find(clientId: Int, platform: Platform): List<Promotion>

}