package com.onepiece.treasure.web.controller

import com.onepiece.treasure.account.model.enums.TopUpState
import com.onepiece.treasure.account.model.enums.WithdrawState
import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/cash")
class CashOrderApiController : BasicController(), CashOrderApi {

    @GetMapping("/topup")
    override fun topup(
            @RequestParam(value = "state", required = false) state: TopUpState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startCreatedTime") startCreatedTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endCreatedTime") endCreatedTime: LocalDateTime
    ): List<TopUpVo> {
        return TopUpValueFactory.generatorTopUoVos()
    }

    @PutMapping("/topup")
    override fun check(@RequestBody topUpUo: TopUpUo) {
    }

    @PutMapping("/topup/enforcement")
    override fun enforcement(@RequestBody topUpUo: TopUpUo) {
    }

    @GetMapping("/withdraw")
    override fun withdraw(
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startCreatedTime") startCreatedTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endCreatedTime") endCreatedTime: LocalDateTime
    ): List<WithdrawVo> {
        return WithdrawValueFactory.generatorWithdrawVos()
    }


    @PutMapping("/withdraw")
    override fun withdrawCheck(@RequestBody withdrawUo: WithdrawUo) {
    }
}