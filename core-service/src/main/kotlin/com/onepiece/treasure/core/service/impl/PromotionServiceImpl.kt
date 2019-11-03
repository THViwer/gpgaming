package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.database.PromotionCo
import com.onepiece.treasure.beans.value.database.PromotionUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.PromotionDao
import com.onepiece.treasure.core.service.PromotionService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class PromotionServiceImpl(
        private val promotionDao: PromotionDao,
        private val redisService: RedisService
) : PromotionService {

    override fun all(clientId: Int): List<Promotion> {

        val redisKey = OnePieceRedisKeyConstant.promotions(clientId)
        return redisService.getList(redisKey, Promotion::class.java) {
            promotionDao.all(clientId)
        }
    }

    override fun create(promotionCo: PromotionCo) {
        val state = promotionDao.create(promotionCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.promotions(promotionCo.clientId))
    }

    override fun update(promotionUo: PromotionUo) {

        val promotion = promotionDao.get(promotionUo.id)

        val state = promotionDao.update(promotionUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.promotions(promotion.clientId))

    }
}