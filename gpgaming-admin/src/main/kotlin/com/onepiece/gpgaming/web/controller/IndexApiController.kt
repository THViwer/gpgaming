package com.onepiece.gpgaming.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.gpgaming.beans.enums.HotGameType
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.PromotionRuleType
import com.onepiece.gpgaming.beans.enums.RecommendedType
import com.onepiece.gpgaming.beans.enums.ShowPosition
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.PromotionRules
import com.onepiece.gpgaming.beans.model.Recommended
import com.onepiece.gpgaming.beans.model.Seo
import com.onepiece.gpgaming.beans.value.database.BannerCo
import com.onepiece.gpgaming.beans.value.database.BannerUo
import com.onepiece.gpgaming.beans.value.database.HotGameValue
import com.onepiece.gpgaming.beans.value.database.I18nContentCo
import com.onepiece.gpgaming.beans.value.database.I18nContentUo
import com.onepiece.gpgaming.beans.value.database.RecommendedValue
import com.onepiece.gpgaming.beans.value.internet.web.BannerCoReq
import com.onepiece.gpgaming.beans.value.internet.web.BannerUoReq
import com.onepiece.gpgaming.beans.value.internet.web.BannerVo
import com.onepiece.gpgaming.beans.value.internet.web.HotGameVo
import com.onepiece.gpgaming.beans.value.internet.web.I18nContentWebValue
import com.onepiece.gpgaming.beans.value.internet.web.PromotionCoReq
import com.onepiece.gpgaming.beans.value.internet.web.PromotionRuleVo
import com.onepiece.gpgaming.beans.value.internet.web.PromotionUoReq
import com.onepiece.gpgaming.beans.value.internet.web.PromotionVo
import com.onepiece.gpgaming.beans.value.internet.web.RecommendedWebValue
import com.onepiece.gpgaming.beans.value.internet.web.SeoValue
import com.onepiece.gpgaming.core.IndexUtil
import com.onepiece.gpgaming.core.service.BannerService
import com.onepiece.gpgaming.core.service.HotGameService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.core.service.RecommendedService
import com.onepiece.gpgaming.core.service.SeoService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexApiController(
        private val bannerService: BannerService,
        private val i18nContentService: I18nContentService,
        private val promotionService: PromotionService,
        private val recommendedService: RecommendedService,
        private val hotGameService: HotGameService,
        private val indexUtil: IndexUtil,
        private val seoService: SeoService,
        private val objectMapper: ObjectMapper
): BasicController(), IndexApi {

    private val log = LoggerFactory.getLogger(IndexApiController::class.java)

    @GetMapping("/seo")
    override fun seo(): Seo {

        val clientId = getClientId()
        return seoService.get(clientId)
    }

    @PutMapping("/seo")
    override fun seo(
            @RequestParam("title") title: String,
            @RequestParam("keywords") keywords: String,
            @RequestParam("description") description: String,
            @RequestParam("liveChatId") liveChatId: String,
            @RequestParam("liveChatTab") liveChatTab: Boolean,
            @RequestParam("googleStatisticsId") googleStatisticsId: String,
            @RequestParam("facebookTr") facebookTr: String,
            @RequestParam("facebookShowPosition") facebookShowPosition: ShowPosition,
            @RequestParam("asgContent") asgContent: String
    ) {
        val clientId = getClientId()

        val seoUo = SeoValue.SeoUo(clientId = clientId, title = title, keywords = keywords, description = description,
                liveChatId = liveChatId, googleStatisticsId = googleStatisticsId, facebookTr = facebookTr, liveChatTab = liveChatTab,
                asgContent = asgContent, facebookShowPosition = facebookShowPosition)
        seoService.update(seoUo)
    }

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

        indexUtil.generatorIndexPage(clientId = getClientId())
    }

    @PutMapping("/i18n")
    override fun update(@RequestBody i18nContentUoReq: I18nContentWebValue.I18nContentUoReq) {

        log.info("国际化修改内容：$i18nContentUoReq")

        val i18nContentUo = I18nContentUo(id = i18nContentUoReq.id, content = i18nContentUoReq.getI18nContent(objectMapper))
        i18nContentService.update(i18nContentUo)

        indexUtil.generatorIndexPage(clientId = getClientId())
    }



    @GetMapping("/banner")
    override fun bannerList(): List<BannerVo> {

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

        indexUtil.generatorIndexPage(clientId = getClientId())
    }



    @GetMapping("/promotion")
    override fun promotionList(): List<PromotionVo> {
        val clientId = getClientId()

        val promotions = promotionService.all(clientId = clientId)
                .filter { it.status != Status.Delete }
        if (promotions.isEmpty()) return emptyList()

        val i18nContentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Promotion).groupBy { it.configId }

        return promotions.map { promotion ->
            val i18nContents = i18nContentMap[promotion.id] ?: emptyList()
//            val defaultContent = i18nContents.first()

            val promotionRuleVo = PromotionRuleVo(ruleType = promotion.ruleType, ruleJson = promotion.ruleJson)

            PromotionVo(id = promotion.id, clientId = promotion.clientId, category = promotion.category, stopTime = promotion.stopTime, top = promotion.top,
                    status = promotion.status, createdTime = promotion.createdTime, updatedTime = promotion.updatedTime, i18nContents = i18nContents,
                    promotionRuleVo = promotionRuleVo, platforms = promotion.platforms, period = promotion.period, periodMaxPromotion = promotion.periodMaxPromotion,
                    levelId = promotion.levelId, sequence = promotion.sequence, show = promotion.show)
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

        indexUtil.generatorIndexPage(clientId = getClientId())
    }


    @GetMapping("/recommended")
    override fun recommendedList(@RequestParam("type") type: RecommendedType): List<Recommended> {
        val clientId = getClientId()
        val recommendeds = recommendedService.getByType(clientId = clientId, type = type)


        return when (type) {
            RecommendedType.IndexSport -> I18nConfig.IndexSport
            RecommendedType.IndexVideo -> I18nConfig.IndexVideo
            else -> null
        }?.let { i18nConfig ->
            i18nContentService.getConfigType(clientId, i18nConfig).groupBy { it.configId }.toMap()
        }?.let { map ->
            recommendeds.map { recommended ->
                val contents = map[recommended.id] ?: emptyList()
                recommended.i18nContents = contents
                recommended
            }
        }?: recommendeds
    }

    @PostMapping("/recommended")
    override fun create(@RequestBody coReq: RecommendedWebValue.CreateReq) {
        val co = RecommendedValue.CreateVo(clientId = getClientId(), type = coReq.type, contentJson = coReq.contentJson, status = Status.Stop)
        recommendedService.create(co = co)
    }

    @PutMapping("/recommended")
    override fun update(@RequestBody uoReq: RecommendedWebValue.UpdateReq) {
        val uo = RecommendedValue.UpdateVo(id = uoReq.id, clientId = getClientId(), contentJson = uoReq.contentJson, status = uoReq.status)
        recommendedService.update(uo = uo)

        indexUtil.generatorIndexPage(clientId = getClientId())
    }

    @GetMapping("/hotGame")
    override fun hotGameList(@RequestParam("type") type: HotGameType): List<HotGameVo> {

        val client = current()
        val contentMap = i18nContentService.getConfigType(clientId = client.clientId, configType = I18nConfig.HotGame)
                .groupBy { it.configId }

        val games = hotGameService.all(clientId = client.clientId).filter { it.type == type }

        return games.map {
            val contents = contentMap[it.id] ?: emptyList()
            HotGameVo(gameId = it.gameId, platform = it.platform, status = it.status, createdTime = it.createdTime,
                    i18nContents = contents, id = it.id)
        }
    }

    @PostMapping("/hotGame")
    override fun create(@RequestBody hotGameCo: HotGameValue.HotGameCo) {
        hotGameService.create(hotGameCo.copy(clientId = getClientId()))
    }

    @PutMapping("/hotGame")
    override fun update(@RequestBody hotGameUo: HotGameValue.HotGameUo) {
        hotGameService.update(hotGameUo)
    }
}