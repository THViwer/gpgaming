package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.WithdrawState
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.ArtificialOrder
import com.onepiece.gpgaming.beans.model.PayBind
import com.onepiece.gpgaming.beans.model.PayOrder
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderCo
import com.onepiece.gpgaming.beans.value.database.ArtificialOrderQuery
import com.onepiece.gpgaming.beans.value.database.DepositLockUo
import com.onepiece.gpgaming.beans.value.database.DepositQuery
import com.onepiece.gpgaming.beans.value.database.PayBindValue
import com.onepiece.gpgaming.beans.value.database.PayOrderValue
import com.onepiece.gpgaming.beans.value.database.WalletNoteQuery
import com.onepiece.gpgaming.beans.value.database.WithdrawQuery
import com.onepiece.gpgaming.beans.value.internet.web.ArtificialCoReq
import com.onepiece.gpgaming.beans.value.internet.web.CashValue
import com.onepiece.gpgaming.beans.value.internet.web.DepositUoReq
import com.onepiece.gpgaming.beans.value.internet.web.DepositVo
import com.onepiece.gpgaming.beans.value.internet.web.TransferOrderValue
import com.onepiece.gpgaming.beans.value.internet.web.WalletNoteValue
import com.onepiece.gpgaming.beans.value.internet.web.WithdrawUoReq
import com.onepiece.gpgaming.beans.value.internet.web.WithdrawVo
import com.onepiece.gpgaming.core.OrderIdBuilder
import com.onepiece.gpgaming.core.service.ArtificialOrderService
import com.onepiece.gpgaming.core.service.DepositService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PayBindService
import com.onepiece.gpgaming.core.service.PayOrderService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.core.service.TransferOrderService
import com.onepiece.gpgaming.core.service.WaiterService
import com.onepiece.gpgaming.core.service.WalletNoteService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.core.service.WithdrawService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/cash")
class CashOrderApiController(
        private val depositService: DepositService,
        private val withdrawService: WithdrawService,
        private val artificialOrderService: ArtificialOrderService,
        private val orderIdBuilder: OrderIdBuilder,
        private val waiterService: WaiterService,
        private val transferOrderService: TransferOrderService,
        private val memberService: MemberService,
        private val walletService: WalletService,
        private val transferUtil: TransferUtil,
        private val payOrderService: PayOrderService,
        private val payBindService: PayBindService,
        private val platformMemberService: PlatformMemberService,
        private val walletNoteService: WalletNoteService
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

        val waiters = waiterService.findClientWaiters(clientId = user.clientId).map {
            it.id to it
        }.toMap()


        val query = DepositQuery(clientId = user.clientId, startTime = null, endTime = null, memberId = null, orderId = null,
                state = DepositState.Process, lockWaiterId = getCurrentWaiterId(), clientBankIdList = clientBankIdList)
        return depositService.query(query).map{
            with(it) {
                DepositVo(id = it.id, orderId = it.orderId, money = money, memberName = memberName, memberBankCardNumber = memberBankCardNumber,
                        memberBank = memberBank, imgPath = imgPath, createdTime = createdTime, remark = remarks, endTime = it.endTime,
                        clientBankId = clientBankId, clientBankCardNumber = clientBankCardNumber, clientBankName = clientBankName,
                        bankOrderId = null, memberId = memberId, state = it.state, lockWaiterId = it.lockWaiterId, depositTime = depositTime,
                        channel = it.channel, username = username, clientBank = it.clientBank, lockWaiterUsername = waiters[it.lockWaiterId?: 0]?.username)
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


        val qMemberId = username?.let {
            memberService.findByUsername(clientId = clientId, username = it)?.id
        }

        val depositQuery = DepositQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId, memberId = qMemberId, state = state,
                lockWaiterId = null, clientBankIdList = null)

        val waiters = waiterService.findClientWaiters(clientId = clientId).map {
            it.id to it
        }.toMap()

        return depositService.query(depositQuery).map {
            with(it) {
                DepositVo(id = it.id, orderId = it.orderId, money = money, memberName = memberName, memberBankCardNumber = memberBankCardNumber,
                        memberBank = memberBank, imgPath = imgPath, createdTime = createdTime, remark = remarks, endTime = it.endTime,
                        clientBankId = clientBankId, clientBankCardNumber = clientBankCardNumber, clientBankName = clientBankName,
                        bankOrderId = null, memberId = memberId, state = it.state, lockWaiterId = it.lockWaiterId, depositTime = depositTime,
                        channel = it.channel, username = it.username, clientBank = it.clientBank, lockWaiterUsername = waiters[it.lockWaiterId?: 0]?.username)
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

        check(current.role == Role.Admin || artificialCoReq.money.toDouble() <= 500) { OnePieceExceptionCode.AUTHORITY_FAIL}

        val member = memberService.getMember(artificialCoReq.memberId)
        check(member.clientId == current.clientId)


        val artificialOrderCo = ArtificialOrderCo(orderId = orderId, clientId = current.clientId, memberId = artificialCoReq.memberId, money = artificialCoReq.money,
                remarks = artificialCoReq.remarks, operatorId = current.id, operatorRole = current.role, operatorUsername = current.username, username = member.username)
        artificialOrderService.create(artificialOrderCo)
    }

    @GetMapping("/artificial")
    override fun artificialList(
            @RequestParam("username", required = false) username: String?,
            @RequestParam("operatorUsername", required = false) operatorUsername: String?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): Page<ArtificialOrder> {

        val currentLogin = this.current()
        val clientId = currentLogin.clientId

        val memberId = if( username != null) {
            memberService.findByUsername(clientId = clientId, username = username)?.id ?: return Page.empty()
        } else null

        val waiterId = if (operatorUsername != null) {
            waiterService.findByUsername(clientId = clientId, username = operatorUsername)?.id ?: return Page.empty()
        } else null

        val queryWaiterId = if (currentLogin.role == Role.Waiter) currentLogin.id else waiterId
        val role = if (currentLogin.role == Role.Waiter) Role.Waiter else null

        val query = ArtificialOrderQuery(clientId = clientId, memberId = memberId, waiterId = queryWaiterId, operatorRole = role, current = current, size = size)
        return artificialOrderService.query(query)
    }

    @GetMapping("/withdraw")
    override fun withdraw(): List<WithdrawVo> {
        val clientId = getClientId()
        val withdrawQuery = WithdrawQuery(clientId = clientId, lockWaiterId = this.getCurrentWaiterId(), startTime = null, endTime = null,
                orderId = null, memberId = null, state = WithdrawState.Process)

        val waiters = waiterService.findClientWaiters(clientId = clientId).map {
            it.id to it
        }.toMap()

        return withdrawService.query(withdrawQuery).map {
            with(it) {
                WithdrawVo(orderId = it.orderId, money = it.money, memberBankId = memberBankId, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberId = memberId,
                        memberName = memberName, state = it.state, remark = remarks, createdTime = createdTime, endTime = endTime, lockWaiterId = it.lockWaiterId, username = username,
                        lockWaiterUsername = waiters[it.lockWaiterId?:0]?.username, id = it.id)
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

        val waiters = waiterService.findClientWaiters(clientId = clientId).map {
            it.id to it
        }.toMap()

        return withdrawService.query(withdrawQuery).map {
            with(it) {
                WithdrawVo(orderId = it.orderId, money = it.money, memberBankId = memberBankId, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberId = memberId,
                        memberName = memberName, state = it.state, remark = remarks, createdTime = createdTime, endTime = it.endTime, lockWaiterId = it.lockWaiterId, username = it.username,
                        lockWaiterUsername = waiters[it.lockWaiterId?:0]?.username, id = it.id)
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

    @GetMapping("/transfer")
    override fun query(
            @RequestParam("promotionId", required = false) promotionId: Int?,
            @RequestParam("memberId", required = false) memberId: Int?,
            @RequestParam("username", required = false) username: String?
    ): List<TransferOrderValue.TransferOrderVo> {
        val user = current()

        val query = TransferOrderValue.Query(clientId = user.clientId, from = null, promotionId = promotionId,
                username = username, memberId = memberId, startDate = null, endDate = null)
        val list = transferOrderService.query(query)

        return list.map { order ->
            TransferOrderValue.TransferOrderVo(orderId = order.orderId, memberId = order.memberId, money = order.money,
                    promotionJson = order.promotionJson, joinPromotionId = order.joinPromotionId, from = order.from, to = order.to,
                    state = order.state, createdTime = order.createdTime, promotionAmount = order.promotionAmount)
        }
    }

    @GetMapping("/wallet/note")
    override fun walletNoteList(
            @RequestParam("memberId") memberId: Int
    ): List<WalletNoteValue.WalletNoteVo> {

        val clientId = getClientId()

//        val memberId = memberService.findByUsername(clientId = clientId, username = username)?.id ?: return

        val query = WalletNoteQuery(clientId = clientId, memberId = memberId, event = null, events = null, startDate = null,
                endDate = null, onlyPromotion = true, current = 0, size = 500)
        val list = walletNoteService.query(query)

        return list.map {
            with(it) {
                WalletNoteValue.WalletNoteVo(eventId = eventId, event = event, money = money, originMoney = originMoney,
                        afterMoney = afterMoney, promotionMoney = promotionMoney, remarks = remarks, createdTime = createdTime)
            }
        }
    }

    @GetMapping("/retrieve")
    override fun retrieve(@RequestParam("memberId") memberId: Int): List<CashValue.BalanceAllInVo> {
        val member = memberService.getMember(memberId)
        return transferUtil.transferInAll(clientId = member.clientId, memberId = memberId, username = member.username)
    }

    @PutMapping("/clean/promotion")
    override fun constraintCleanPromotion(
            @RequestParam("memberId") memberId: Int,
            @RequestParam("platform") platform: Platform
    ) {
        // 清理平台会员优惠信息
        platformMemberService.cleanTransferIn(memberId = memberId, platform = platform)
    }

    @GetMapping("/payBind")
    override fun payBind(): List<PayBind> {
        return payBindService.list(clientId = this.getClientId())
    }

    @PostMapping("/payBind")
    override fun payBindCreate(@RequestBody req: PayBindValue.PayBindCo) {
        payBindService.create(req.copy(clientId = this.getClientId()))
    }

    @PutMapping("/payBind")
    override fun payBindUpdate(@RequestBody req: PayBindValue.PayBindUo) {
        payBindService.update(req.copy(clientId = this.getClientId()))
    }

    @GetMapping("/thirdpay")
    override fun payOrder(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam("payType", required = false) payType: PayType?,
            @RequestParam("orderId", required = false) orderId: String?,
            @RequestParam("username", required = false) username: String?,
            @RequestParam("state", required = false) state: PayState?
    ): List<PayOrder> {
        val user = this.current()

        val query = PayOrderValue.PayOrderQuery(clientId = user.clientId, memberId = null, username = username,
                state = state, startDate = startDate, endDate = endDate, current = 0, size = 500, orderId = orderId,
                payType = payType, memberIds = null)
        return payOrderService.query(query)
    }

    @PutMapping("/thirdpay")
    override fun thirdPayCheck(
            @RequestParam("orderId") orderId: String,
            @RequestParam("remark") remark: String
    ) {

        val user = this.current()

        val uo = PayOrderValue.ConstraintUo(orderId = orderId, operatorId = user.id, operatorUsername = user.username, remark = remark)
        payOrderService.check(uo)
    }
}