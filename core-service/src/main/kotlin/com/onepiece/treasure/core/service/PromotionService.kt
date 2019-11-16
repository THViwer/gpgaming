package com.onepiece.treasure.core.service

//import com.onepiece.treasure.beans.model.PromotionRule
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.internet.web.PromotionCoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionUoReq

interface PromotionService {

    fun all(clientId: Int): List<Promotion>

    fun create(clientId: Int, promotionCoReq: PromotionCoReq)

    fun update(clientId: Int, promotionUoReq: PromotionUoReq)

    fun get(id: Int): Promotion

    fun find(clientId: Int, platform: Platform): List<Promotion>

//    fun getCurrentPromotion(clientId: Int, platform: Platform): PromotionRule?
}