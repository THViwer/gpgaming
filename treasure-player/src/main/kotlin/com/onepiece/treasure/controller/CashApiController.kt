package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.DepositState
import com.onepiece.treasure.beans.enums.WalletEvent
import com.onepiece.treasure.beans.enums.WithdrawState
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.beans.value.internet.web.DepositVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawVo
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import com.onepiece.treasure.core.OrderIdBuilder
import com.onepiece.treasure.core.service.*
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/cash")
class CashApiController(
        private val memberBankService: MemberBankService,
        private val depositService: DepositService,
        private val withdrawService: WithdrawService,
        private val clientBankService: ClientBankService,
        private val orderIdBuilder: OrderIdBuilder,
        private val walletService: WalletService,
        private val memberService: MemberService
) : BasicController(), CashApi {


    @GetMapping("/bank")
    override fun banks(): List<MemberBankVo> {
        return memberBankService.query(id).map {
            with(it) {
                MemberBankVo(id = id, name = name, bank = bank, bankCardNumber = bankCardNumber, status = status,
                        createdTime = createdTime, clientId = clientId, memberId = memberId)
            }
        }
    }

    @PostMapping("/bank")
    override fun bankCreate(@RequestBody memberBankCoReq: MemberBankCoReq) {

        val memberBankCo = MemberBankCo(clientId = clientId, memberId = id, bank = memberBankCoReq.bank, name = memberBankCoReq.name,
                bankCardNumber = memberBankCoReq.bankCardNumber)
        memberBankService.create(memberBankCo)
    }

    @PutMapping("/bank")
    override fun bankUpdate(@RequestBody memberBankUoReq: MemberBankUoReq) {
        val memberBankUo = MemberBankUo(id = memberBankUoReq.id, bank = memberBankUoReq.bank, bankCardNumber = memberBankUoReq.bankCardNumber,
                status = memberBankUoReq.status)
        memberBankService.update(memberBankUo)

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

        val depositQuery = DepositQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId,
                memberId = id, state = state)

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


    @PutMapping("/deposit")
    override fun deposit(@RequestBody depositCoReq: DepositCoReq): CashDepositResp {

        val clientBank = clientBankService.get(depositCoReq.clientBankId)

        val orderId = orderIdBuilder.generatorDepositOrderId()

        val depositCo = DepositCo(orderId = orderId, memberId = id, memberName = depositCoReq.memberName, memberBankCardNumber = depositCoReq.memberBankCardNumber,
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


        val withdrawQuery = WithdrawQuery(clientId = clientId, startTime = startTime, endTime = endTime, orderId = orderId,
                memberId = id, state = state)

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

    @PutMapping("/withdraw")
    override fun withdraw(@RequestBody withdrawCoReq: WithdrawCoReq): CashWithdrawResp {

        // check bank id
        val memberBank = memberBankService.query(id).find { it.id == withdrawCoReq.memberBankId }
        checkNotNull(memberBank) { OnePieceExceptionCode.AUTHORITY_FAIL }

        // check safety password
        memberService.checkSafetyPassword(id = id, safetyPassword = withdrawCoReq.safetyPassword)

        // create order
        val orderId = orderIdBuilder.generatorWithdrawOrderId()
        val withdrawCo = WithdrawCo(orderId = orderId, clientId = clientId, memberId = id, memberName = memberBank.name,
                memberBank = memberBank.bank, memberBankCardNumber = memberBank.bankCardNumber, memberBankId = memberBank.id,
                money = withdrawCoReq.money, remarks = null)
        withdrawService.create(withdrawCo)

        return CashWithdrawResp(orderId = orderId)
    }

    @PutMapping("/transfer")
    override fun transfer(@RequestBody cashTransferReq: CashTransferReq) {

        val platformMemberVo = getPlatformMember(cashTransferReq.platform)
        val platformMember = platformMemberService.get(platformMemberVo.id)

        when (cashTransferReq.action) {
            // 中心钱包 -> 平台钱包
            TransferAction.TransferIn -> {

                // 活动赠送金额
                val giftBalance = BigDecimal.ZERO

                // 中心钱包扣款
                val walletUo = WalletUo(clientId = clientId, memberId = id, event = WalletEvent.TRANSFER_OUT, money = cashTransferReq.money, remarks = "transfer center to platform")
                walletService.update(walletUo)

                //TODO 调用平台接口充值

                // 平台钱包更改信息
                val demandBet = if (giftBalance == BigDecimal.ZERO) {
                    BigDecimal.ZERO
                } else {
                    giftBalance.plus(cashTransferReq.money).multiply(BigDecimal.valueOf(1.2))
                }
                val platformMemberTransferUo = PlatformMemberTransferUo(id = platformMember.id, money = cashTransferReq.money, giftBalance = giftBalance, demandBet = demandBet)
                platformMemberService.transferIn(platformMemberTransferUo)
            }
            // 平台钱包 -> 中心钱包
            TransferAction.TransferOut -> {

                // 检查是否满足打码量
                check(platformMember.currentBet > platformMember.demandBet) { OnePieceExceptionCode.TRANSFER_OUT_BET_FAIL }
                // 检查余额
                val wallet = walletService.getMemberWallet(id)
                check(wallet.balance.toDouble() - cashTransferReq.money.toDouble() > 0) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }

                // 中心钱包加钱
                val walletUo = WalletUo(clientId = clientId, memberId = id, event = WalletEvent.TRANSFER_IN, money = cashTransferReq.money, remarks = "transfer platform to center")
                walletService.update(walletUo)

                //TODO 调用平台接口取款
            }
        }
    }
}