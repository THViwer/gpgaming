package com.onepiece.treasure.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.enums.PromotionRuleType
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.I18nContent
import com.onepiece.treasure.beans.model.PromotionRules
import com.onepiece.treasure.beans.value.database.BannerCo
import com.onepiece.treasure.beans.value.database.BannerUo
import com.onepiece.treasure.beans.value.database.I18nContentCo
import com.onepiece.treasure.beans.value.database.I18nContentUo
import com.onepiece.treasure.beans.value.internet.web.*
import com.onepiece.treasure.core.service.BannerService
import com.onepiece.treasure.core.service.I18nContentService
import com.onepiece.treasure.core.service.PromotionService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
class IndexApiController(
        private val bannerService: BannerService,
        private val i18nContentService: I18nContentService,
        private val promotionService: PromotionService,
        private val objectMapper: ObjectMapper
): BasicController(), IndexApi {

    private val log = LoggerFactory.getLogger(IndexApiController::class.java)

    @GetMapping("/i18n/languages")
    override fun languages(): List<Language> {
        return Language.values().toList()
    }

    @GetMapping("/i18n/announcement")
    override fun announcementList(): List<I18nContent> {
        val clientId = getClientId()
        return i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Announcement)
    }

    @PostMapping("/i18n")
    override fun create(@RequestBody i18nContentCoReq: I18nContentWebValue.I18nContentCoReq) {
        val clientId = getClientId()

        val i18nContentCo = I18nContentCo(clientId = clientId, content = i18nContentCoReq.getI18nContent(objectMapper),
                language = i18nContentCoReq.language, configId = i18nContentCoReq.configId, configType = i18nContentCoReq.configType)

        i18nContentService.create(i18nContentCo)
    }

    @PutMapping("/i18n")
    override fun update(@RequestBody i18nContentUoReq: I18nContentWebValue.I18nContentUoReq) {
        val i18nContentUo = I18nContentUo(id = i18nContentUoReq.id, content = i18nContentUoReq.getI18nContent(objectMapper))
        i18nContentService.update(i18nContentUo)
    }



    @GetMapping("/banner")
    override fun banerList(): List<BannerVo> {

        val clientId = getClientId()

        val map = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Banner)
                .groupBy { it.configId }

        return bannerService.all(getClientId()).map {
            val contents = map[it.id]?: emptyList()

            BannerVo(id = it.id, clientId = it.clientId, order = it.order, type = it.type, link = it.link, status = it.status,
                    createdTime = it.createdTime, updatedTime = it.updatedTime, contents = contents)
        }.filter { it.status != Status.Delete }
    }

    @PostMapping("/banner")
    override fun create(@RequestBody bannerCoReq: BannerCoReq) {
        val advertCo = BannerCo(clientId = getClientId(), type = bannerCoReq.type,
                order = bannerCoReq.order, link = bannerCoReq.link)
        bannerService.create(advertCo)
    }

    @PutMapping("/banner")
    override fun update(@RequestBody bannerUoReq: BannerUoReq) {
        val bannerUo = BannerUo(id = bannerUoReq.id, type = bannerUoReq.type,
                order = bannerUoReq.order, link = bannerUoReq.link, status = bannerUoReq.status)
        bannerService.update(bannerUo)
    }



    @GetMapping("/promotion")
    override fun promotionList(): List<PromotionVo> {
        val clientId = getClientId()

        val promotions = promotionService.all(clientId = clientId)
        if (promotions.isEmpty()) return emptyList()

        val i18nContentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Promotion).groupBy { it.configId }

        return promotions.map { promotion ->
            val i18nContents = i18nContentMap[promotion.id] ?: error(OnePieceExceptionCode.DATA_FAIL)
//            val defaultContent = i18nContents.first()

            val promotionRuleVo = PromotionRuleVo(ruleType= promotion.ruleType, levelId = promotion.levelId, ruleJson = promotion.ruleJson)

            PromotionVo(id = promotion.id, clientId = promotion.clientId, category = promotion.category, stopTime = promotion.stopTime, top = promotion.top,
                    icon = promotion.icon, status = promotion.status,
                    createdTime = promotion.createdTime, updatedTime = promotion.updatedTime, i18nContents = i18nContents, promotionRuleVo = promotionRuleVo,
                    platforms = promotion.platforms)
        }
    }

    @PostMapping("/promotion")
    override fun create(@RequestBody promotionCoReq: PromotionCoReq) {

        val clientId = getClientId()

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

//        check(promotionCoReq.i18nContent.language == Language.EN) { OnePieceExceptionCode.DATA_FAIL }

        promotionService.create(clientId = clientId, promotionCoReq = promotionCoReq)
    }

    @PutMapping("/promotion")
    override fun update(@RequestBody promotionUoReq: PromotionUoReq) {
        val clientId = getClientId()

        promotionService.update(clientId = clientId, promotionUoReq = promotionUoReq)
    }



}