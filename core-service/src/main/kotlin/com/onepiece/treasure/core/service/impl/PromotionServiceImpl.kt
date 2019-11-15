package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.database.I18nContentCo
import com.onepiece.treasure.beans.value.database.PromotionCo
import com.onepiece.treasure.beans.value.database.PromotionUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.PromotionDao
import com.onepiece.treasure.core.service.I18nContentService
import com.onepiece.treasure.core.service.PromotionService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class PromotionServiceImpl(
        private val promotionDao: PromotionDao,
        private val redisService: RedisService,
        private val i18nContentService: I18nContentService
) : PromotionService {

    override fun all(clientId: Int): List<Promotion> {

        val redisKey = OnePieceRedisKeyConstant.promotions(clientId)
        return redisService.getList(redisKey, Promotion::class.java) {
            promotionDao.all(clientId)
        }
    }

    override fun create(promotionCo: PromotionCo) {
        val id = promotionDao.create(promotionCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        val i18nContentCo = I18nContentCo(clientId = promotionCo.clientId, title = promotionCo.title, synopsis = promotionCo.synopsis,
                content = promotionCo.content, configId = id, configType = I18nConfig.Promotion, language = promotionCo.language)
        i18nContentService.create(i18nContentCo)

        redisService.delete(OnePieceRedisKeyConstant.promotions(promotionCo.clientId))
    }

    override fun update(promotionUo: PromotionUo) {

        val promotion = promotionDao.get(promotionUo.id)

        val state = promotionDao.update(promotionUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.promotions(promotion.clientId))

    }
}