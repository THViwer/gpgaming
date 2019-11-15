package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.database.BannerCo
import com.onepiece.treasure.beans.value.database.BannerUo
import com.onepiece.treasure.beans.value.internet.web.BannerCoReq
import com.onepiece.treasure.beans.value.internet.web.BannerUoReq
import com.onepiece.treasure.beans.value.internet.web.BannerVo
import com.onepiece.treasure.core.service.BannerService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/advert")
class BannerApiController(
        private val advertService: BannerService
) : BasicController(), BannerApi {

    @GetMapping
    override fun all(): List<BannerVo> {
        return advertService.all(clientId).map {
            BannerVo(id = it.id, clientId = it.clientId, icon = it.icon, touchIcon = it.touchIcon, order = it.order,
                    type = it.type, link = it.link, status = it.status, createdTime = it.createdTime,
                    updatedTime = it.updatedTime)
        }
    }

    @PostMapping
    override fun create(@RequestBody bannerCoReq: BannerCoReq) {

        val advertCo = BannerCo(clientId = clientId, icon = bannerCoReq.icon, touchIcon = bannerCoReq.touchIcon, type = bannerCoReq.type,
                order = bannerCoReq.order, link = bannerCoReq.link)
        advertService.create(advertCo)
    }

    @PutMapping
    override fun update(@RequestBody bannerUoReq: BannerUoReq) {

        val bannerUo = BannerUo(id = bannerUoReq.id, icon = bannerUoReq.icon, touchIcon = bannerUoReq.touchIcon, type = bannerUoReq.type,
                order = bannerUoReq.order, link = bannerUoReq.link, status = bannerUoReq.status)
        advertService.update(bannerUo)
    }
}