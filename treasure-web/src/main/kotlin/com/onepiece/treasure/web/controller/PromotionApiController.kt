package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.internet.web.PromotionCoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionRuleVo
import com.onepiece.treasure.beans.value.internet.web.PromotionUoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionVo
import com.onepiece.treasure.core.dao.PromotionRuleDao
import com.onepiece.treasure.core.service.I18nContentService
import com.onepiece.treasure.core.service.PromotionService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/promotion")
class PromotionApiController(
        private val promotionService: PromotionService,
        private val i18nContentService: I18nContentService,
        private val promotionRuleDao: PromotionRuleDao
) : BasicController(), PromotionApi {

    @GetMapping
    override fun all(): List<PromotionVo> {

        val promotions = promotionService.all(clientId = clientId)
        if (promotions.isEmpty()) return emptyList()

        val i18nContentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Promotion).groupBy { it.configId }

        val promotionIds = promotions.map { it.id }
        val promotionRuleMap = promotionRuleDao.getByPromotionIds(promotionIds).map { it.promotionId to it }.toMap()

        return promotions.map { promotion ->
            val i18nContents = i18nContentMap[promotion.id] ?: error(OnePieceExceptionCode.DATA_FAIL)
            val defaultContent = i18nContents.first()

            val promotionRule = promotionRuleMap[promotion.id] ?: error(OnePieceExceptionCode.DATA_FAIL)
            val promotionRuleVo = PromotionRuleVo(category = promotionRule.category, levelId = promotionRule.levelId, ruleJson = promotionRule.ruleJson)

            PromotionVo(id = promotion.id, clientId = promotion.clientId, category = promotion.category, stopTime = promotion.stopTime, top = promotion.top,
                    icon = promotion.icon, title = defaultContent.title, synopsis = defaultContent.synopsis, content = defaultContent.content, status = promotion.status,
                    createdTime = promotion.createdTime, updatedTime = promotion.updatedTime, i18nContents = i18nContents, promotionRuleVo = promotionRuleVo)
        }
    }

    @PostMapping
    override fun create(@RequestBody promotionCoReq: PromotionCoReq) {
        promotionService.create(clientId = clientId, promotionCoReq = promotionCoReq)
    }

    @PutMapping
    override fun update(@RequestBody promotionUoReq: PromotionUoReq) {
        promotionService.update(clientId = clientId, promotionUoReq = promotionUoReq)
    }
}