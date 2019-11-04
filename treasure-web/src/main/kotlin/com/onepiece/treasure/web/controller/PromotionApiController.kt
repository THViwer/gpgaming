package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.database.PromotionCo
import com.onepiece.treasure.beans.value.database.PromotionUo
import com.onepiece.treasure.beans.value.internet.web.PromotionCoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionUoReq
import com.onepiece.treasure.beans.value.internet.web.PromotionVo
import com.onepiece.treasure.core.service.PromotionService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/promotion")
class PromotionApiController(
        private val promotionService: PromotionService
) : BasicController(), PromotionApi {

    @GetMapping
    override fun all(): List<PromotionVo> {
        return promotionService.all(clientId = clientId).map {
            PromotionVo(id = it.id, clientId = it.clientId, category = it.category, stopTime = it.stopTime, top = it.top, icon = it.icon,
                    title = it.title, synopsis = it.synopsis, content = it.content, status = it.status, createdTime = it.createdTime,
                    updatedTime = it.updatedTime)
        }
    }

    @PostMapping
    override fun create(@RequestBody promotionCoReq: PromotionCoReq) {

        val promotionCo = PromotionCo(clientId = clientId, category = promotionCoReq.category, content = promotionCoReq.content,
                title = promotionCoReq.title, synopsis = promotionCoReq.synopsis, icon = promotionCoReq.icon, top = promotionCoReq.top,
                stopTime = promotionCoReq.stopTime)

        promotionService.create(promotionCo)
    }

    @PutMapping
    override fun update(@RequestBody promotionUoReq: PromotionUoReq) {

        val promotionUo = PromotionUo(id = promotionUoReq.id, category = promotionUoReq.category, content = promotionUoReq.content,
                title = promotionUoReq.title, synopsis = promotionUoReq.synopsis, icon = promotionUoReq.icon, top = promotionUoReq.top,
                stopTime = promotionUoReq.stopTime)
        promotionService.update(promotionUo)
    }
}