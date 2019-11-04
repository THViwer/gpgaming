package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.database.AnnouncementCo
import com.onepiece.treasure.beans.value.database.AnnouncementUo
import com.onepiece.treasure.beans.value.internet.web.AnnouncementCoReq
import com.onepiece.treasure.beans.value.internet.web.AnnouncementUoReq
import com.onepiece.treasure.beans.value.internet.web.AnnouncementVo
import com.onepiece.treasure.core.service.AnnouncementService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/announcement")
class AnnouncementApiController(
        private val announcementService: AnnouncementService
) : BasicController(), AnnouncementApi {

    @GetMapping
    override fun all(): List<AnnouncementVo> {
        return announcementService.all(clientId).map {
            AnnouncementVo(id = it.id, clientId = it.clientId, title = it.title, content = it.content, createdTime = it.createdTime, updatedTime = it.updatedTime)
        }
    }

    @PostMapping
    override fun create(@RequestBody announcementCoReq: AnnouncementCoReq) {
        val announcementCo = AnnouncementCo(clientId = clientId, title = announcementCoReq.title, content = announcementCoReq.content)
        announcementService.create(announcementCo)
    }

    @PutMapping
    override fun update(@RequestBody announcementUoReq: AnnouncementUoReq) {
        val announcementUo = AnnouncementUo(id = announcementUoReq.id, title = announcementUoReq.title, content = announcementUoReq.content)
        announcementService.update(announcementUo)
    }
}