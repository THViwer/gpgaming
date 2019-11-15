package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.internet.web.PromotionCoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionUoReq

interface PromotionService {

    fun all(clientId: Int): List<Promotion>

    fun create(clientId: Int, promotionCoReq: PromotionCoReq)

    fun update(clientId: Int, promotionUoReq: PromotionUoReq)
}