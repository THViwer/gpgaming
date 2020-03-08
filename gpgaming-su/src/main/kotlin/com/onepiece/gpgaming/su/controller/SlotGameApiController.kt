package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.SlotGame
import com.onepiece.gpgaming.beans.value.database.SlotGameValue
import com.onepiece.gpgaming.core.service.SlotGameService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/slot")
class SlotGameApiController(
        private val slotGameService: SlotGameService
) : SlotGameApi {

    @PostMapping
    override fun create(@RequestBody co: SlotGameValue.SlotGameCo) {
        slotGameService.create(slotGameCo = co)
    }

    @PutMapping
    override fun update(@RequestBody uo: SlotGameValue.SlotGameUo) {
        slotGameService.update(uo)
    }

    @GetMapping
    override fun list(@RequestParam("platform") platform: Platform): List<SlotGame> {
        return slotGameService.findByPlatform(platform = platform)
    }
}