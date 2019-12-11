package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.Role
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.ArtificialOrderCo
import com.onepiece.treasure.beans.value.database.DepositLockUo
import com.onepiece.treasure.beans.value.database.DepositQuery
import com.onepiece.treasure.beans.value.database.WithdrawQuery
import com.onepiece.treasure.beans.value.internet.web.*
import com.onepiece.treasure.core.OrderIdBuilder
import com.onepiece.treasure.core.service.ArtificialOrderService
import com.onepiece.treasure.core.service.DepositService
import com.onepiece.treasure.core.service.WaiterService
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
        private val orderIdBuilder: OrderIdBuilder,
        private val waiterService: WaiterService
) : BasicController(), CashOrderApi {


    @GetMapping("/check")
    override fun check(): List<CashValue.CheckOrderVo> {
        val clientId = getClientId()

        val depositQuery = DepositQuery(clientId = clientId, startTime = null,
                endTime = null, memberId = null, orderId = null, state = DepositState.Process, lockWaiterId = getCurrentWaiterId(), clientBankIdList = null)
        val deposits = depositService.query(depositQuery).map { CashValue.CheckOrderVo.of(it) }

        val withdrawQuery = WithdrawQuery(clientId = clientId, lockWaiterId = this.getCurrentWaiterId(), startTime = null, endTime = null,
                orderId = null, memberId = null, state = WithdrawState.Process)
        val withdraws = withdrawService.query(withdrawQuery).map { CashValue.CheckOrderVo.of(it) }

        return deposits.plus(withdraws).sortedBy { it.applicationTime }

    }

    @PutMapping("/check/lock")
    override fun checkLock(@RequestBody req: CashValue.CheckOrderLockReq) {

        val current = this.current()
        val orderId = req.orderId

        when (req.type) {
            CashValue.Type.Deposit -> {
                val order = depositService.findDeposit(current.clientId, orderId)
                check(order.state ==  DepositState.Close || order.state == DepositState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

                val depositLockUo = DepositLockUo(clientId = current.clientId, orderId = orderId, processId = order.processId, lockWaiterId = current.id,
                        lockWaiterName = current.username)
                depositService.lock(depositLockUo)
            }
            CashValue.Type.Withdraw -> {
                val order = withdrawService.findWithdraw(current.clientId, orderId)
                check( order.state == WithdrawState.Process) { OnePieceExceptionCode.ORDER_EXPIRED }

                val depositLockUo = DepositLockUo(clientId = current.clientId, orderId = orderId, processId = order.processId, lockWaiterId = current.id,
                        lockWaiterName = current.musername)
                withdrawService.lock(depositLockUo)
            }
        }
    }

    @PutMapping("/check")
    override fun check(@RequestBody req: CashValue.CheckOrderReq) {

        val current = this.current()
        check(req.state == CashValue.State.Fail || req.state == CashValue.State.Successful) { OnePieceExceptionCode.DATA_FAIL }

        when (req.type) {
            CashValue.Type.Deposit -> {
                val depositReq = DepositUoReq(orderId = req.orderId, state = DepositState.valueOf(req.state.toString()), remarks = req.remark, clientId = current.clientId, waiterId = current.id)
                depositService.check(depositReq)
            }
            CashValue.Type.Withdraw -> {
                val withdrawReq = WithdrawUoReq(orderId = req.orderId, state = WithdrawState.valueOf(req.state.toString()), remarks = req.remark, clientId = current.clientId, waiterId = current.id)
                withdrawService.check(withdrawReq)
            }
        }

    }

    @GetMapping("/deposit")
    override fun deposit(): List<DepositVo> {

        val user = current()

        val clientBankIdList = when (user.role) {
            Role.Client -> {
                null
            }
            Role.Waiter -> {
                val waiter = waiterService.get(user.id)
                val clientBanks = waiter.clientBanks
                if (clientBanks.isNullOrEmpty()) return emptyList()
                clientBanks
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        val query = DepositQuery(clientId = user.clientId, startTime = null, endTime = null, memberId = null, orderId = null,
                state = DepositState.Process, lockWaiterId = getCurrentWaiterId(), clientBankIdList = clientBankIdList)
        return depositService.query(query).map{
            with(it) {
                DepositVo(orderId = it.orderId, money = money, memberName = memberName, memberBankCardNumber= memberBankCardNumber,
                        memberBank=  memberBank, imgPath = imgPath, createdTime = createdTime, remark = remarks, endTime = it.endTime,
                        clientBankId = clientBankId, clientBankCardNumber = clientBankCardNumber, clientBankName = clientBankName,
                        bankOrderId = null, memberId = memberId, state = it.state, lockWaiterId = it.lockWaiterId, depositTime = depositTime,
                        channel = it.channel, username = username, clientBank = it.clientBank)
            }
        }
    }

    @GetMapping("/deposit/history")
    override fun deposit(
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): List<DepositVo> {
        val clientId = getClientId()
        val depositQuery = DepositQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId, memberId = null, state = state,
                lockWaiterId = null, clientBankIdList = null)
        return depositService.query(depositQuery).map {
            with(it) {
                DepositVo(orderId = it.orderId, money = money, memberName = memberName, memberBankCardNumber= memberBankCardNumber,
                        memberBank=  memberBank, imgPath = imgPath, createdTime = createdTime, remark = remarks, endTime = it.endTime,
                        clientBankId = clientBankId, clientBankCardNumber = clientBankCardNumber, clientBankName = clientBankName,
                        bankOrderId = null, memberId = memberId, state = it.state, lockWaiterId = it.lockWaiterId, depositTime = depositTime,
                        channel = it.channel, username = it.username, clientBank = it.clientBank)
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

    @PutMapping("/artificial")
    override fun artificial(@RequestBody artificialCoReq: ArtificialCoReq) {
        val current = current()
        val orderId = orderIdBuilder.generatorArtificialOrderId()
        val artificialOrderCo = ArtificialOrderCo(orderId = orderId, clientId = current.clientId, memberId = artificialCoReq.memberId, money = artificialCoReq.money,
                remarks = artificialCoReq.remarks, operatorId = current.id, operatorRole = current.role)
        artificialOrderService.create(artificialOrderCo)
    }


    @GetMapping("/withdraw")
    override fun withdraw(): List<WithdrawVo> {
        val clientId = getClientId()
        val withdrawQuery = WithdrawQuery(clientId = clientId, lockWaiterId = this.getCurrentWaiterId(), startTime = null, endTime = null,
                orderId = null, memberId = null, state = WithdrawState.Process)

        return withdrawService.query(withdrawQuery).map {
            with(it) {
                WithdrawVo(orderId = it.orderId, money = it.money, memberBankId = memberBankId, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberId = memberId,
                        memberName = memberName, state = it.state, remark = remarks, createdTime = createdTime, endTime = endTime, lockWaiterId = it.lockWaiterId, username = username)
            }
        }

    }

    @GetMapping("/withdraw/history")
    override fun withdraw(
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "username", required = false) username: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") endTime: LocalDateTime
    ): List<WithdrawVo> {

        val clientId = getClientId()
        val withdrawQuery = WithdrawQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId, memberId = null, state = state, lockWaiterId = null)
        return withdrawService.query(withdrawQuery).map {

            with(it) {
                WithdrawVo(orderId = it.orderId, money = it.money, memberBankId = memberBankId, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberId = memberId,
                        memberName = memberName, state = it.state, remark = remarks, createdTime = createdTime, endTime = endTime, lockWaiterId = it.lockWaiterId, username = it.username)
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