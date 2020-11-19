package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.AppDown
import com.onepiece.gpgaming.beans.value.database.AppDownValue
import com.onepiece.gpgaming.beans.value.internet.web.AppDownWebValue
import com.onepiece.gpgaming.core.service.AppDownService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
                status = Status.Normal, createdTime = LocalDateTime.now(), icon = coReq.icon)

        appDownService.create(appDown)
    }

    @PutMapping
    override fun update(@RequestBody update: AppDownValue.Update) {
        appDownService.update(update)
    }
}