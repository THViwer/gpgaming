package com.onepiece.treasure.web.controller

import com.onepiece.treasure.account.model.enums.TopUpState
import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.TopUpUo
import com.onepiece.treasure.web.controller.value.TopUpValueFactory
import com.onepiece.treasure.web.controller.value.TopUpVo
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/cash")
class CashOrderApiController : BasicController(), CashOrderApi {

    @GetMapping("/topup")
    override fun topup(
            @RequestParam("state") state: TopUpState?,
            @RequestParam("orderId") orderId: String?,
            @RequestParam("username") username: String,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("minCreatedTime") minCreatedTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("maxCreatedTime") maxCreatedTime: LocalDateTime
    ): List<TopUpVo> {
        return TopUpValueFactory.generatorTopUoVos()
    }

    @PutMapping("/topup")
    override fun check(@RequestBody topUpUo: TopUpUo) {
    }

    @PutMapping("/topup/enforcement")
    override fun enforcement(@RequestBody topUpUo: TopUpUo) {
    }
}