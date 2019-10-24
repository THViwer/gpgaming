package com.onepiece.treasure.web.controller

import com.onepiece.treasure.core.model.enums.DepositState
import com.onepiece.treasure.core.model.enums.WithdrawState
import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/cash")
class CashOrderApiController : BasicController(), CashOrderApi {

    @GetMapping("/deposit")
    override fun deposit(
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime
    ): List<DepositVo> {
        return DepositValueFactory.generatorDeposits()
    }

    @PutMapping("/deposit")
    override fun check(@RequestBody depositUo: DepositUo) {
    }

    @PutMapping("/deposit/enforcement")
    override fun enforcement(@RequestBody depositUo: DepositUo) {
    }

    @GetMapping("/withdraw")
    override fun withdraw(
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime
    ): List<WithdrawVo> {
        return WithdrawValueFactory.generatorWithdrawVos()
    }


    @PutMapping("/withdraw")
    override fun withdrawCheck(@RequestBody withdrawUo: WithdrawUo) {
    }
}