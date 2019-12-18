package com.onepiece.treasure.su.controller

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.AppDown
import com.onepiece.treasure.beans.value.database.AppDownValue
import com.onepiece.treasure.beans.value.internet.web.AppDownWebValue
import com.onepiece.treasure.core.service.AppDownService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/app")
class AppDownApiController(
        private val appDownService: AppDownService
): AppDownApi {

    @GetMapping
    override fun list(): List<AppDown> {
        return appDownService.all()
    }

    @PostMapping
    override fun create(@RequestBody coReq: AppDownWebValue.CoReq) {
        val appDown = AppDown(id = -1, platform = coReq.platform, iosPath = coReq.iosPath, androidPath = coReq.androidPath,
                status = Status.Normal, createdTime = LocalDateTime.now())

        appDownService.create(appDown)
    }

    @PutMapping
    override fun update(@RequestBody update: AppDownValue.Update) {
        appDownService.update(update)
    }
}