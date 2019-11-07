package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.beans.value.internet.web.ClientBankVo
import com.onepiece.treasure.beans.value.internet.web.DepositVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawVo
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import com.onepiece.treasure.core.OrderIdBuilder
import com.onepiece.treasure.core.service.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/cash")
open class CashApiController(
        private val memberBankService: MemberBankService,
        private val depositService: DepositService,
        private val withdrawService: WithdrawService,
        private val clientBankService: ClientBankService,
        private val orderIdBuilder: OrderIdBuilder,
        private val walletService: WalletService,
        private val memberService: MemberService,
        private val transferOrderService: TransferOrderService,
        private val clientService: ClientService
) : BasicController(), CashApi {


    @GetMapping("/bank")
    override fun banks(): List<MemberBankVo> {
        val memberId = this.current().id
        return memberBankService.query(memberId).map {
            with(it) {
                MemberBankVo(id = id, name = name, bank = bank, bankCardNumber = bankCardNumber, status = status,
                        createdTime = createdTime, clientId = clientId, memberId = memberId)
            }
        }
    }

    @PostMapping("/bank")
    override fun bankCreate(@RequestBody memberBankCoReq: MemberBankCoReq) {

        val (clientId, memberId) = this.currentClientIdAndMemberId()
        val memberBankCo = MemberBankCo(clientId = clientId, memberId = memberId, bank = memberBankCoReq.bank, name = memberBankCoReq.name,
                bankCardNumber = memberBankCoReq.bankCardNumber)
        memberBankService.create(memberBankCo)
    }

    @PutMapping("/bank")
    override fun bankUpdate(@RequestBody memberBankUoReq: MemberBankUoReq) {
        val memberBankUo = MemberBankUo(id = memberBankUoReq.id, bank = memberBankUoReq.bank, bankCardNumber = memberBankUoReq.bankCardNumber,
                status = memberBankUoReq.status)
        memberBankService.update(memberBankUo)
    }

    @GetMapping("/client/banks")
    override fun clientBanks(): List<ClientBankVo> {
        return clientBankService.findClientBank(current().clientId).filter { it.status == Status.Normal }.map {
            with(it) {
                ClientBankVo(id = id, bank = bank, bankName = bank.cname, name = name, bankCardNumber = bankCardNumber,
                        status = status, createdTime = createdTime)
            }
        }
    }

    @GetMapping("/deposit")
    override fun deposit(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<DepositVo> {

        val (clientId, memberId) = this.currentClientIdAndMemberId()

        val depositQuery = DepositQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId,
                memberId = memberId, state = state)

        val page = depositService.query(depositQuery, current, size)
        if (page.total == 0) return Page.empty()

        val data = page.data.map {
            with(it) {
                DepositVo(orderId = it.orderId, money = money, state = it.state, remark = remarks, createdTime = createdTime,
                        endTime = endTime, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberName = memberName,
                        imgPath = imgPath, memberId = memberId, bankOrderId = null, clientBankCardNumber = clientBankCardNumber,
                        clientBankName = clientBankName, clientBankId = clientBankId)
            }
        }

        return Page.of(page.total, data)
    }


    @PostMapping("/deposit")
    override fun deposit(@RequestBody depositCoReq: DepositCoReq): CashDepositResp {

        val clientBank = clientBankService.get(depositCoReq.clientBankId)
        val orderId = orderIdBuilder.generatorDepositOrderId()

        val (clientId, memberId) = this.currentClientIdAndMemberId()
        val depositCo = DepositCo(orderId = orderId, memberId = memberId, memberName = depositCoReq.memberName, memberBankCardNumber = depositCoReq.memberBankCardNumber,
                memberBank = depositCoReq.memberBank, clientId = clientId, clientBankId = clientBank.id, clientBankName = clientBank.name,
                clientBankCardNumber = clientBank.bankCardNumber, money = depositCoReq.money, imgPath = depositCoReq.imgPath)
        depositService.create(depositCo)

        return CashDepositResp(orderId = orderId)
    }

    @GetMapping("/withdraw")
    override fun withdraw(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startTime") startTime: LocalDateTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endTime") endTime: LocalDateTime,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WithdrawVo> {


        val (clientId, memberId) = this.currentClientIdAndMemberId()

        val withdrawQuery = WithdrawQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId,
                memberId = memberId, state = state)

        val page = withdrawService.query(withdrawQuery, current, size)
        if (page.total == 0) return Page.empty()

        val data = page.data.map {
            with(it) {
                WithdrawVo(orderId = it.orderId, money = money, state = it.state, remark = remarks, createdTime = createdTime,
                        endTime = endTime, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberName = memberName,
                        memberId = it.memberId, memberBankId = it.memberBankId)
            }
        }

        return Page.of(page.total, data)
    }

    @PostMapping("/withdraw")
    override fun withdraw(@RequestBody withdrawCoReq: WithdrawCoReq): CashWithdrawResp {

        val (clientId, memberId) = currentClientIdAndMemberId()

        // check bank id
        val memberBank = memberBankService.query(memberId).find { it.id == withdrawCoReq.memberBankId }
        checkNotNull(memberBank) { OnePieceExceptionCode.AUTHORITY_FAIL }

        // check safety password
        memberService.checkSafetyPassword(id = memberId, safetyPassword = withdrawCoReq.safetyPassword)

        // create order
        val orderId = orderIdBuilder.generatorWithdrawOrderId()
        val withdrawCo = WithdrawCo(orderId = orderId, clientId = clientId, memberId = memberId, memberName = memberBank.name,
                memberBank = memberBank.bank, memberBankCardNumber = memberBank.bankCardNumber, memberBankId = memberBank.id,
                money = withdrawCoReq.money, remarks = null)
        withdrawService.create(withdrawCo)

        return CashWithdrawResp(orderId = orderId)
    }

    @PutMapping("/transfer")
    @Transactional(rollbackFor = [Exception::class])
    override fun transfer(@RequestBody cashTransferReq: CashTransferReq) {
        check(cashTransferReq.from != cashTransferReq.to) { OnePieceExceptionCode.AUTHORITY_FAIL }

        val (clientId, memberId) = currentClientIdAndMemberId()


        val platformMember = if (cashTransferReq.from == Platform.Center) {
            val platformMemberVo = getPlatformMember(cashTransferReq.to)
            platformMemberService.get(platformMemberVo.id)
        } else {
            val platformMemberVo = getPlatformMember(cashTransferReq.from)
            platformMemberService.get(platformMemberVo.id)
        }

        when (cashTransferReq.from) {
            // 中心钱包 -> 平台钱包
            Platform.Center -> {

                // 活动赠送金额
                val giftBalance = BigDecimal.ZERO

                // 检查保证金是否足够
                clientService.updateEarnestBalance(id = clientId, earnestBalance = cashTransferReq.money.negate())

                // 中心钱包扣款
                val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_OUT, money = cashTransferReq.money,
                        remarks = "transfer center to platform", waiterId = null, eventId = null)
                walletService.update(walletUo)

                // 生成转账订单
                val transferOrderId = orderIdBuilder.generatorTransferOrderId()
                val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = cashTransferReq.money, giftMoney = giftBalance,
                        from = cashTransferReq.from, to = cashTransferReq.to)
                transferOrderService.create(transferOrderCo)


                //TODO 调用平台接口充值
                gamePlatformUtil.getPlatformBuild(cashTransferReq.to).gameCashApi
                        .transfer(getClientAuthVo(cashTransferReq.to),platformMember.username, transferOrderId, cashTransferReq.money.plus(giftBalance))

                // 平台钱包更改信息
                val demandBet = if (giftBalance == BigDecimal.ZERO) {
                    BigDecimal.ZERO
                } else {
                    giftBalance.plus(cashTransferReq.money).multiply(BigDecimal.valueOf(1.2))
                }
                val platformMemberTransferUo = PlatformMemberTransferUo(id = platformMember.id, money = cashTransferReq.money, giftBalance = giftBalance, demandBet = demandBet)
                platformMemberService.transferIn(platformMemberTransferUo)

                // 更新转账订单
                val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = TransferState.Successful)
                transferOrderService.update(transferOrderUo)

            }
            // 平台钱包 -> 中心钱包
            else -> {

                // 检查是否满足打码量
                check(platformMember.currentBet >= platformMember.demandBet) { OnePieceExceptionCode.TRANSFER_OUT_BET_FAIL }
                // 检查余额
                val wallet = walletService.getMemberWallet(memberId)
                check(wallet.balance.toDouble() - cashTransferReq.money.toDouble() > 0) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }


                // 检查保证金是否足够
                clientService.updateEarnestBalance(id = clientId, earnestBalance = cashTransferReq.money)

                // 生成转账订单
                val transferOrderId = orderIdBuilder.generatorTransferOrderId()
                val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = cashTransferReq.money, giftMoney = BigDecimal.ZERO,
                        from = cashTransferReq.from, to = cashTransferReq.to)
                transferOrderService.create(transferOrderCo)

                // 中心钱包加钱
                val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_IN, money = cashTransferReq.money,
                        remarks = "transfer platform to center", waiterId = null, eventId = null)
                walletService.update(walletUo)

                //TODO 调用平台接口取款
                gamePlatformUtil.getPlatformBuild(cashTransferReq.from).gameCashApi
                        .transfer(getClientAuthVo(cashTransferReq.from), platformMember.username, transferOrderId, cashTransferReq.money.negate())

                // 更新转账订单
                val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = TransferState.Successful)
                transferOrderService.update(transferOrderUo)
            }
        }
    }

    @GetMapping("/balance")
    override fun balance(@RequestHeader platform: Platform): BigDecimal {
        return when (platform) {
            Platform.Center -> walletService.getMemberWallet(current().id).balance
            else -> {
                val platformMemberVo = getPlatformMember(platform)
                gamePlatformUtil.getPlatformBuild(platform).gameCashApi.wallet(username = platformMemberVo.platformUsername)
            }
        }
    }
}
