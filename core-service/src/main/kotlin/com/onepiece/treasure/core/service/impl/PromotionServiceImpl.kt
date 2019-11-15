package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.beans.value.internet.web.PromotionCoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionUoReq
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.PromotionDao
import com.onepiece.treasure.core.dao.PromotionRuleDao
import com.onepiece.treasure.core.service.I18nContentService
import com.onepiece.treasure.core.service.PromotionService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PromotionServiceImpl(
        private val promotionDao: PromotionDao,
        private val redisService: RedisService,
        private val i18nContentService: I18nContentService,
        private val promotionRoleDao: PromotionRuleDao
) : PromotionService {

    override fun all(clientId: Int): List<Promotion> {

        val redisKey = OnePieceRedisKeyConstant.promotions(clientId)
        return redisService.getList(redisKey, Promotion::class.java) {
            promotionDao.all(clientId)
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun create(clientId: Int, promotionCoReq: PromotionCoReq) {

        // 创建优惠记录
        val promotionCo = PromotionCo(clientId = clientId, category = promotionCoReq.category, stopTime = promotionCoReq.stopTime, top = promotionCoReq.top,
                icon = promotionCoReq.icon)
        val promotionId = promotionDao.create(promotionCo)
        check(promotionId > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 创建国际化
        val i18nContent = promotionCoReq.i18nContent
        val i18nContentCo = I18nContentCo(clientId = promotionCo.clientId, title = i18nContent.title, synopsis = i18nContent.synopsis,
                content = i18nContent.content, configId = promotionId, configType = I18nConfig.Promotion, language = i18nContent.language)
        val i18nContentId = i18nContentService.create(i18nContentCo)
        check(i18nContentId > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 创建优惠活动
        val promotionRuleVo = promotionCoReq.PromotionRuleVo
        val promotionRuleCo = PromotionRuleCo(clientId = clientId, platform = promotionCoReq.platform, category = promotionRuleVo.category,
                levelId = promotionRuleVo.levelId, ruleJson = promotionRuleVo.ruleJson, promotionId = promotionId)
        val promotionRoleState = promotionRoleDao.create(promotionRuleCo)
        check(promotionRoleState) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.promotions(promotionCo.clientId))
    }

    override fun update(clientId: Int, promotionUoReq: PromotionUoReq) {

        val promotion = promotionDao.get(promotionUoReq.id)

        // 更新优惠记录
        val promotionUo = PromotionUo(id = promotionUoReq.id, category = promotionUoReq.category, stopTime = promotionUoReq.stopTime,
                top = promotionUoReq.top, icon = promotionUoReq.icon, status = promotionUoReq.status)
        val state = promotionDao.update(promotionUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // 更新优惠活动
        if (promotionUoReq.ruleJson != null) {
            val promotionRuleUo = PromotionRuleUo(promotionId = promotionUoReq.id , ruleJson = promotionUoReq.ruleJson)
            promotionRoleDao.update(promotionRuleUo)
        }

        redisService.delete(OnePieceRedisKeyConstant.promotions(promotion.clientId))

    }
}