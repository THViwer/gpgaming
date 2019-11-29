package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.internet.web.DepositUoReq
import com.onepiece.treasure.beans.value.internet.web.DepositVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawUoReq
import com.onepiece.treasure.beans.value.internet.web.WithdrawVo
import com.onepiece.treasure.core.OrderIdBuilder
import com.onepiece.treasure.core.service.ArtificialOrderService
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
        private val withdrawService: WithdrawService,
        private val artificialOrderService: ArtificialOrderService,
        private val orderIdBuilder: OrderIdBuilder
) : BasicController(), CashOrderApi {

    @GetMapping("/deposit")
    override fun deposit(
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): List<DepositVo> {
        val clientId = getClientId()
        val depositQuery = DepositQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId, memberId = null, state = state)
        return depositService.query(depositQuery).map {
            with(it) {
                DepositVo(orderId = it.orderId, money = money, memberName = memberName, memberBankCardNumber= memberBankCardNumber,
                        memberBank=  memberBank, imgPath = imgPath, createdTime = createdTime, remark = remarks, endTime = it.endTime,
                        clientBankId = clientBankId, clientBankCardNumber = clientBankCardNumber, clientBankName = clientBankName,
                        bankOrderId = null, memberId = memberId, state = it.state, lockWaiterId = it.lockWaiterId)
            }
        }
    }

    @PutMapping("/deposit/lock")
    override fun tryLock(@RequestParam("orderId") orderId: String) {
        val current = current()
        val order = depositService.findDeposit(current.clientId, orderId)
        check(order.state ==  DepositState.Close || order.state == DepositState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

        val depositLockUo = DepositLockUo(clientId = current.clientId, orderId = orderId, processId = order.processId, lockWaiterId = current.id,
                lockWaiterName = current.username)
        depositService.lock(depositLockUo)
    }

    @PutMapping("/deposit")
    override fun check(@RequestBody depositUoReq: DepositUoReq) {
        val current = this.current()
        val req = depositUoReq.copy(clientId = current.clientId, waiterId = current.id)
        depositService.check(req)
    }

//    @PutMapping("/deposit/artificial")
//    override fun artificial(@RequestBody artificialCoReq: ArtificialCoReq) {
//        val orderId = orderIdBuilder.generatorArtificialOrderId()
//        val artificialOrderCo = ArtificialOrderCo(orderId = orderId, clientId = clientId, memberId = artificialCoReq.memberId, money = artificialCoReq.money,
//                remarks = artificialCoReq.remarks, operatorId = waiterId, operatorRole = role)
//        artificialOrderService.create(artificialOrderCo)
//    }

    @GetMapping("/withdraw")
    override fun withdraw(
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): List<WithdrawVo> {

        val clientId = getClientId()
        val withdrawQuery = WithdrawQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId, memberId = null, state = state)
        return withdrawService.query(withdrawQuery).map {

            with(it) {
                WithdrawVo(orderId = it.orderId, money = it.money, memberBankId = memberBankId, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberId = memberId,
                        memberName = memberName, state = it.state, remark = remarks, createdTime = createdTime, endTime = endTime, lockWaiterId = it.lockWaiterId)
            }
        }
    }

    @PutMapping("/withdraw/lock")
    override fun withdrawLock(@RequestParam("orderId") orderId: String) {
        val current = this.current()
        val order = withdrawService.findWithdraw(current.clientId, orderId)
        check( order.state == WithdrawState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

        val depositLockUo = DepositLockUo(clientId = current.clientId, orderId = orderId, processId = order.processId, lockWaiterId = current.id,
                lockWaiterName = current.musername)
        withdrawService.lock(depositLockUo)
    }

    @PutMapping("/withdraw")
    override fun withdrawCheck(@RequestBody withdrawUoReq: WithdrawUoReq) {
        val current = this.current()
        val req = withdrawUoReq.copy(clientId = current.clientId, waiterId = current.id)
        withdrawService.check(req)

    }
}