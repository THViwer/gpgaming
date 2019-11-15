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
import java.time.LocalDate

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
        private val walletNoteService: WalletNoteService
) : BasicController(), CashApi {


    @GetMapping("/bank")
    override fun banks(): List<Bank> {
        return Bank.values().toList()
    }

    @GetMapping("/bank/my")
    override fun myBanks(): List<MemberBankVo> {
        val member = this.current()

        // 我的银行卡列表
        val myBankMap = memberBankService.query(member.id).map { it.bank to it }.toMap()

        return Bank.values().map {
            val myBank = myBankMap[it]

            when (myBank != null) {
                true -> {
                    MemberBankVo(id = myBank.id, name = myBank.name, bank = myBank.bank, bankCardNumber = myBank.bankCardNumber,
                            clientId = member.clientId, memberId = member.id)
                }
                else -> {
                    MemberBankVo(id = -1, name = it.cname, bank = it, bankCardNumber = null, clientId = member.clientId, memberId = member.clientId)
                }
            }
        }
    }

    @PostMapping("/bank/my")
    override fun bankCreate(@RequestBody memberBankCoReq: MemberBankCoReq) {

        val (clientId, memberId) = this.currentClientIdAndMemberId()
        val memberBankCo = MemberBankCo(clientId = clientId, memberId = memberId, bank = memberBankCoReq.bank, name = memberBankCoReq.name,
                bankCardNumber = memberBankCoReq.bankCardNumber)
        memberBankService.create(memberBankCo)
    }

    @PutMapping("/bank/my")
    override fun bankUpdate(@RequestBody memberBankUoReq: MemberBankUoReq) {
        val memberBankUo = MemberBankUo(id = memberBankUoReq.id, bank = memberBankUoReq.bank, bankCardNumber = memberBankUoReq.bankCardNumber,
                status = memberBankUoReq.status)
        memberBankService.update(memberBankUo)
    }

    @GetMapping("/bank/client")
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
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<DepositVo> {

        val (clientId, memberId) = this.currentClientIdAndMemberId()

        val depositQuery = DepositQuery(clientId = clientId, startTime = startDate.atStartOfDay(), endTime = endDate.atStartOfDay(), orderId = orderId,
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
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("endDate") endDate: LocalDate,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WithdrawVo> {


        val (clientId, memberId) = this.currentClientIdAndMemberId()

        val withdrawQuery = WithdrawQuery(clientId = clientId, startTime = startDate.atStartOfDay(), endTime = endDate.atStartOfDay(), orderId = orderId,
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

        // 获得用户银行卡Id
        val memberBankId = if (withdrawCoReq.memberBankId == null) {
            val memberBankCo = MemberBankCo(clientId = clientId, memberId = memberId, bank = withdrawCoReq.bank, name = withdrawCoReq.name,
                    bankCardNumber = withdrawCoReq.bankCardNumber)
            memberBankService.create(memberBankCo)
        } else withdrawCoReq.memberBankId

        // check bank id
        val memberBank = memberBankService.query(memberId).find { it.id == memberBankId }
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
//                clientService.updateEarnestBalance(id = clientId, earnestBalance = cashTransferReq.money.negate())
                platformBindService.updateEarnestBalance(clientId = clientId, platform = cashTransferReq.to, earnestBalance = cashTransferReq.money.negate())


                val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = cashTransferReq.to)

                // 中心钱包扣款
                val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_OUT, money = cashTransferReq.money,
                        remarks = "Center => ${cashTransferReq.to}", waiterId = null, eventId = transferOrderId)
                walletService.update(walletUo)

                // 生成转账订单
                val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = cashTransferReq.money, giftMoney = giftBalance,
                        from = cashTransferReq.from, to = cashTransferReq.to)
                transferOrderService.create(transferOrderCo)


                //TODO 调用平台接口充值
                gameApi.transfer(clientId = clientId, platformUsername = platformMember.username, orderId = transferOrderId, amount = cashTransferReq.money.plus(giftBalance),
                        platform = cashTransferReq.to)


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
//                clientService.updateEarnestBalance(id = clientId, earnestBalance = cashTransferReq.money)
                platformBindService.updateEarnestBalance(clientId = clientId, platform = cashTransferReq.from, earnestBalance = cashTransferReq.money)


                // 生成转账订单
                val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = cashTransferReq.from)
                val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = cashTransferReq.money, giftMoney = BigDecimal.ZERO,
                        from = cashTransferReq.from, to = cashTransferReq.to)
                transferOrderService.create(transferOrderCo)

                // 中心钱包加钱
                val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_IN, money = cashTransferReq.money,
                        remarks = "${cashTransferReq.from} => Center", waiterId = null, eventId = transferOrderId)
                walletService.update(walletUo)

                //TODO 调用平台接口取款
                gameApi.transfer(clientId = clientId, platformUsername = platformMember.username, orderId = transferOrderId, amount = cashTransferReq.money.negate(),
                        platform = cashTransferReq.from)

                // 更新转账订单
                val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = TransferState.Successful)
                transferOrderService.update(transferOrderUo)
            }
        }
    }

    @GetMapping("/wallet/note")
    override fun walletNote(): List<WalletNoteVo> {

        val member = this.current()

        return walletNoteService.my(clientId = member.clientId, memberId = member.id).map {
            WalletNoteVo(id = it.id, memberId = it.memberId, eventId = it.eventId, event = it.event, money = it.money, remarks = it.remarks, createdTime = it.createdTime)
        }

    }

    @GetMapping("/balance")
    override fun balance(@RequestHeader platform: Platform): BalanceVo {
        val member = current()
        val balance =  when (platform) {
            Platform.Center -> walletService.getMemberWallet(current().id).balance
            else -> {
                val platformMemberVo = getPlatformMember(platform)
                gameApi.balance(clientId = member.clientId, platformUsername = platformMemberVo.platformUsername, platform = platform)
            }
        }
        return BalanceVo(platform = platform, balance = balance, transfer = false, tips = null)
    }

    @GetMapping
    override fun balances(): List<BalanceVo> {

        val member = this.current()
        val clientId = member.clientId
        val memberId = member.id

        // 查询主钱包
        val wallet = walletService.getMemberWallet(memberId = memberId)
        val walletBalanceVo = BalanceVo(platform = Platform.Center, balance = wallet.balance, transfer = false, tips = null)

        // 查询厅主开通的平台列表
        val platforms = platformBindService.findClientPlatforms(clientId)

        // 查询用户开通的平台列表
        val platformMemberMap = platformMemberService.findPlatformMember(memberId = memberId).map { it.platform to it }.toMap()


        // 查询余额 //TODO 暂时没用async
        val balances = platforms.map {
            val platformMember = platformMemberMap[it.platform]

            when (platformMember == null) {
                true -> BalanceVo(platform = it.platform, balance = BigDecimal.ZERO, transfer = false, tips = null)
                else -> {
                    val balance = gameApi.balance(clientId = clientId, platformUsername = platformMember.username, platform = it.platform)

                    //TODO 暂时不处理是否可以转账和提示信息
                    BalanceVo(platform = it.platform, balance = balance, transfer = false, tips = null)
                }
            }
        }

        return balances.plus(walletBalanceVo)
    }
}





























