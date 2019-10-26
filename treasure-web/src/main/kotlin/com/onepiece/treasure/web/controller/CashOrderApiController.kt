package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.internet.web.*
import com.onepiece.treasure.core.service.DepositService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/cash")
class CashOrderApiController(
        private val depositService: DepositService,
        private val withdrawService: DepositService
) : BasicController(), CashOrderApi {

    @GetMapping("/deposit")
    override fun deposit(
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime
    ): List<DepositVo> {
        val depositQuery = DepositQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId, memberId = null, state = state)
        return depositService.query(depositQuery).map {
            with(it) {
                DepositVo(orderId = it.orderId, money = money, bank = bank, bankCardNumber = bankCardNumber, name = name, imgPath = imgPath,
                        createdTime = createdTime, remark = remarks, endTime = endTime, clientBankId = clientBankId, clientBankCardNumber = clientBankCardNumber,
                        clientBankName = clientBankName, bankOrderId = null, memberId = memberId, state = it.state)
            }
        }
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