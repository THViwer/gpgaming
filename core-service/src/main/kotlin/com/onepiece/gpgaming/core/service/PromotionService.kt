package com.onepiece.gpgaming.core.service

//import com.onepiece.treasure.beans.model.PromotionRule
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.Promotion
import com.onepiece.gpgaming.beans.value.internet.web.PromotionCoReq
import com.onepiece.gpgaming.beans.value.internet.web.PromotionUoReq

interface PromotionService {

    fun all(clientId: Int): List<Promotion>

    fun create(clientId: Int, promotionCoReq: PromotionCoReq)

    fun update(clientId: Int, promotionUoReq: PromotionUoReq)

    // if id == -100 获得平台: 918kiss、pussy、mega平台的优惠
    fun get(id: Int): Promotion

    fun find(clientId: Int, platform: Platform): List<Promotion>


//    fun getCurrentPromotion(clientId: Int, platform: Platform): PromotionRule?
}