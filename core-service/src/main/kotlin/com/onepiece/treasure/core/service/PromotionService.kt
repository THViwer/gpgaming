package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.database.PromotionCo
import com.onepiece.treasure.beans.value.database.PromotionUo

interface PromotionService {

    fun all(clientId: Int): List<Promotion>

    fun create(promotionCo: PromotionCo)

    fun update(promotionUo: PromotionUo)
}