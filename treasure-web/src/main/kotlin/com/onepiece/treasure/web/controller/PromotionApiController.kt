package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.PromotionCo
import com.onepiece.treasure.beans.value.database.PromotionUo
import com.onepiece.treasure.beans.value.internet.web.PromotionCoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionUoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionVo
import com.onepiece.treasure.core.service.I18nContentService
import com.onepiece.treasure.core.service.PromotionService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/promotion")
class PromotionApiController(
        private val promotionService: PromotionService,
        private val i18nContentService: I18nContentService
) : BasicController(), PromotionApi {

    @GetMapping
    override fun all(): List<PromotionVo> {

        val promotions = promotionService.all(clientId = clientId)
        if (promotions.isEmpty()) return emptyList()

        val i18nContentMap = i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Promotion).groupBy { it.configId }

        return promotions.map { promotion ->
            val i18nContents = i18nContentMap[promotion.id] ?: error(OnePieceExceptionCode.DATA_FAIL)
            val defaultContent = i18nContents.first()
            PromotionVo(id = promotion.id, clientId = promotion.clientId, category = promotion.category, stopTime = promotion.stopTime, top = promotion.top,
                    icon = promotion.icon, title = defaultContent.title, synopsis = defaultContent.synopsis, content = defaultContent.content, status = promotion.status,
                    createdTime = promotion.createdTime, updatedTime = promotion.updatedTime, i18nContents = i18nContents)
        }
    }

    @PostMapping
    override fun create(@RequestBody promotionCoReq: PromotionCoReq) {

        val promotionCo = PromotionCo(clientId = clientId, category = promotionCoReq.category, content = promotionCoReq.content,
                title = promotionCoReq.title, synopsis = promotionCoReq.synopsis, icon = promotionCoReq.icon, top = promotionCoReq.top,
                stopTime = promotionCoReq.stopTime, language = promotionCoReq.language)

        promotionService.create(promotionCo)
    }

    @PutMapping
    override fun update(@RequestBody promotionUoReq: PromotionUoReq) {

        val promotionUo = PromotionUo(id = promotionUoReq.id, category = promotionUoReq.category, icon = promotionUoReq.icon, top = promotionUoReq.top,
                stopTime = promotionUoReq.stopTime)
        promotionService.update(promotionUo)
    }
}