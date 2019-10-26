package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.beans.value.internet.web.DepositUoReq
import com.onepiece.treasure.beans.value.internet.web.DepositVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawUoReq
import com.onepiece.treasure.beans.value.internet.web.WithdrawVo
import com.onepiece.treasure.core.service.DepositService
import com.onepiece.treasure.core.service.WithdrawService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/cash")
class CashOrderApiController(
        private val depositService: DepositService,
        private val withdrawService: WithdrawService
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
                DepositVo(orderId = it.orderId, money = money, memberName = memberName, memberBankCardNumber= memberBankCardNumber,
                        memberBank=  memberBank, imgPath = imgPath, createdTime = createdTime, remark = remarks, endTime = endTime,
                        clientBankId = clientBankId, clientBankCardNumber = clientBankCardNumber, clientBankName = clientBankName,
                        bankOrderId = null, memberId = memberId, state = it.state)
            }
        }
    }

    @GetMapping("/deposit/lock")
    override fun tryLock(@RequestParam("orderId") orderId: String) {
        val order = depositService.findDeposit(clientId, orderId)
        check(order.state ==  DepositState.Close || order.state == DepositState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

        val depositLockUo = DepositLockUo(clientId = clientId, orderId = orderId, processId = order.processId, lockWaiterId = id, lockWaiterName = name)
        depositService.lock(depositLockUo)
    }

    @PutMapping("/deposit")
    override fun check(@RequestBody depositUoReq: DepositUoReq) {

        val order = depositService.findDeposit(clientId, depositUoReq.orderId)
        check(order.state ==  DepositState.Close || order.state == DepositState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

        val depositUo = DepositUo(orderId = depositUoReq.orderId, processId = order.processId, state = depositUoReq.state,
                remarks = depositUoReq.remark)
        depositService.update(depositUo)
    }

//    @PutMapping("/deposit/enforcement")
//    override fun enforcement(@RequestBody depositUoReq: DepositUoReq) {
//    }

    @GetMapping("/withdraw")
    override fun withdraw(
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime
    ): List<WithdrawVo> {

        val withdrawQuery = WithdrawQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId, memberId = null, state = state)
        return withdrawService.query(withdrawQuery).map {

            with(it) {
                WithdrawVo(orderId = it.orderId, money = it.money, memberBankId = memberBankId, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberId = memberId,
                        memberName = memberName, state = it.state, remark = remarks, createdTime = createdTime, endTime = endTime)
            }
        }
    }

    @GetMapping("/withdraw/lock")
    override fun withdrawLock(@RequestParam("orderId") orderId: String) {
        val order = withdrawService.findWithdraw(clientId, orderId)
        check( order.state == WithdrawState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

        val depositLockUo = DepositLockUo(clientId = clientId, orderId = orderId, processId = order.processId, lockWaiterId = id, lockWaiterName = name)
        withdrawService.lock(depositLockUo)
    }

    @PutMapping("/withdraw")
    override fun withdrawCheck(@RequestBody withdrawUoReq: WithdrawUoReq) {
        val order = withdrawService.findWithdraw(clientId, withdrawUoReq.orderId)
        check( order.state == WithdrawState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

        val withdrawUo = WithdrawUo(orderId = withdrawUoReq.orderId, processId = order.processId, state = withdrawUoReq.state, remarks = withdrawUoReq.remarks)
        withdrawService.update(withdrawUo)

    }
}