package com.onepiece.treasure.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.enums.PromotionRuleType
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.PromotionRules
import com.onepiece.treasure.beans.value.internet.web.PromotionCoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionRuleVo
import com.onepiece.treasure.beans.value.internet.web.PromotionUoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionVo
import com.onepiece.treasure.core.service.I18nContentService
import com.onepiece.treasure.core.service.PromotionService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/promotion")
class PromotionApiController(
        private val promotionService: PromotionService,
        private val i18nContentService: I18nContentService,
        private val objectMapper: ObjectMapper
//        private val promotionRuleDao: PromotionRuleDao
) : BasicController(), PromotionApi {

    private val log = LoggerFactory.getLogger(PromotionApiController::class.java)

    @GetMapping
    override fun all(): List<PromotionVo> {

        val promotions = promotionService.all(clientId = clientId)
        if (promotions.isEmpty()) return emptyList()

        val i18nContentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Promotion).groupBy { it.configId }

//        val promotionIds = promotions.map { it.id }
//        val promotionRuleMap = promotionRuleDao.getByPromotionIds(promotionIds).map { it.promotionId to it }.toMap()

        return promotions.map { promotion ->
            val i18nContents = i18nContentMap[promotion.id] ?: error(OnePieceExceptionCode.DATA_FAIL)
            val defaultContent = i18nContents.first()

            val promotionRuleVo = PromotionRuleVo(ruleType= promotion.ruleType, levelId = promotion.levelId, ruleJson = promotion.ruleJson)

            PromotionVo(id = promotion.id, clientId = promotion.clientId, category = promotion.category, stopTime = promotion.stopTime, top = promotion.top,
                    icon = promotion.icon, title = defaultContent.title, synopsis = defaultContent.synopsis, content = defaultContent.content, status = promotion.status,
                    createdTime = promotion.createdTime, updatedTime = promotion.updatedTime, i18nContents = i18nContents, promotionRuleVo = promotionRuleVo)
        }
    }

    @PostMapping
    override fun create(@RequestBody promotionCoReq: PromotionCoReq) {
        // 校验数据的准确性
        val ruleJson = promotionCoReq.promotionRuleVo.ruleJson

        try {
            when (promotionCoReq.promotionRuleVo.ruleType) {
                PromotionRuleType.Bet -> {
                    objectMapper.readValue<PromotionRules.BetRule>(ruleJson)
                }
                PromotionRuleType.Withdraw -> {
                    objectMapper.readValue<PromotionRules.WithdrawRule>(ruleJson)

                }
                else -> error(OnePieceExceptionCode.DATA_FAIL)
            }
        } catch (e: Exception) {
            log.error("优惠活动规则校验失败,类型：${promotionCoReq.promotionRuleVo.ruleType}, json: ${promotionCoReq.promotionRuleVo.ruleJson}")
            error(OnePieceExceptionCode.PROMOTION_JSON_DATA_FAIL)
        }

        promotionService.create(clientId = clientId, promotionCoReq = promotionCoReq)
    }

    @PutMapping
    override fun update(@RequestBody promotionUoReq: PromotionUoReq) {
        promotionService.update(clientId = clientId, promotionUoReq = promotionUoReq)
    }
}