package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.database.AdvertCo
import com.onepiece.treasure.beans.value.database.AdvertUo
import com.onepiece.treasure.beans.value.internet.web.AdvertCoReq
import com.onepiece.treasure.beans.value.internet.web.AdvertUoReq
import com.onepiece.treasure.beans.value.internet.web.AdvertVo
import com.onepiece.treasure.core.service.AdvertService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/advert")
class AdvertApiController(
        private val advertService: AdvertService
) : BasicController(), AdvertApi {

    @GetMapping
    override fun all(): List<AdvertVo> {
        return advertService.all(clientId).map {
            AdvertVo(id = it.id, clientId = it.clientId, icon = it.icon, touchIcon = it.touchIcon, order = it.order,
                    position = it.position, link = it.link, status = it.status, createdTime = it.createdTime,
                    updatedTime = it.updatedTime)
        }
    }

    @PostMapping
    override fun create(@RequestBody advertCoReq: AdvertCoReq) {

        val advertCo = AdvertCo(clientId = clientId, icon = advertCoReq.icon, touchIcon = advertCoReq.touchIcon, position = advertCoReq.position,
                order = advertCoReq.order, link = advertCoReq.link)
        advertService.create(advertCo)
    }

    @PutMapping
    override fun update(@RequestBody advertUoReq: AdvertUoReq) {

        val advertUo = AdvertUo(id = advertUoReq.id, icon = advertUoReq.icon, touchIcon = advertUoReq.touchIcon, position = advertUoReq.position,
                order = advertUoReq.order, link = advertUoReq.link, status = advertUoReq.status)
        advertService.update(advertUo)
    }
}