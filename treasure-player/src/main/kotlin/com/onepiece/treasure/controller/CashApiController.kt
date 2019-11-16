package com.onepiece.treasure.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.model.PromotionRules
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
        private val walletNoteService: WalletNoteService,
        private val promotionService: PromotionService,
        private val objectMapper: ObjectMapper
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

        when (cashTransferReq.from) {
            // 中心钱包 -> 平台钱包
            Platform.Center -> {
                centerToPlatformTransfer(clientId = clientId, memberId = memberId, platform = cashTransferReq.to, amount = cashTransferReq.money, joinPromotion = cashTransferReq.joinPromotion)
            }
            // 平台钱包 -> 中心钱包
            else -> {

                val platform = cashTransferReq.from
                val platformMemberVo = this.getPlatformMember(platform)

                // 获得当前钱包余额
                val balance = gameApi.balance(clientId = clientId, platformUsername = platformMemberVo.platformUsername, platform = platform)
                platformToCenterTransfer(clientId = clientId, memberId = memberId, platform = platform, amount = balance)
            }
        }
    }

    // 中心转平台
    private fun centerToPlatformTransfer(clientId: Int, memberId: Int, platform: Platform, amount: BigDecimal, joinPromotion: Boolean) {

        val from = Platform.Center
        val to = platform

        // 获得平台用户信息
        val platformMemberVo = getPlatformMember(platform)
        val platformMember = platformMemberService.get(platformMemberVo.id)

        // 转账订单编号
        val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = platform)

        // 检查是否可以转入
        val state = this.checkPlatformToCenterTransfer(amount = amount, platform = platform)
        check(state) { OnePieceExceptionCode.CENTER_TO_PLATFORM_FAIl }

        // 优惠活动赠送金额
        val init = PlatformMemberTransferUo(id = platformMember.id, joinPromotionId = null, currentBet = BigDecimal.ZERO, requirementBet = BigDecimal.ZERO, promotionAmount = BigDecimal.ZERO,
                transferAmount = amount, requirementTransferOutAmount = BigDecimal.ZERO, ignoreTransferOutAmount = BigDecimal.ZERO)
        val platformMemberTransferUo = if (joinPromotion) {
            this.getJoinPromotion(clientId = clientId, platform = platform, amount = amount, init = init)
        } else init


        // 如果有优惠活动 那么原来的平台钱包必须没有钱
        if (platformMemberTransferUo.joinPromotionId != null) {
            val platformBalance = gameApi.balance(clientId = clientId, platform = platform, platformUsername = getPlatformMember(platform).platformUsername)
            check(platformBalance.toDouble() <= 0) { OnePieceExceptionCode.PLATFORM_HAS_BALANCE_PROMOTION_FAIL }

        }

        // 检查保证金是否足够
        platformBindService.updateEarnestBalance(clientId = clientId, platform = platform, earnestBalance = amount.negate())

        // 中心钱包扣款
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_OUT, money = amount,
                remarks = "Center => $platform", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)

        // 生成转账订单
        val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = amount, promotionAmount = platformMemberTransferUo.promotionAmount,
                from = from, to = to, joinPromotionId = platformMemberTransferUo.joinPromotionId)
        transferOrderService.create(transferOrderCo)

        // 平台钱包更改信息
        platformMemberService.transferIn(platformMemberTransferUo)




        //调用平台接口充值
        gameApi.transfer(clientId = clientId, platformUsername = platformMember.username, orderId = transferOrderId, amount = amount.plus(platformMemberTransferUo.promotionAmount),
                platform = to)

        // 更新转账订单
        val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = TransferState.Successful)
        transferOrderService.update(transferOrderUo)
    }


    private fun checkPlatformToCenterTransfer(platform: Platform, amount: BigDecimal): Boolean {
        val platformMemberVo = getPlatformMember(platform)
        val platformMember = platformMemberService.get(platformMemberVo.id)

        return when {
            platformMember.joinPromotionId == null -> true
            platformMember.ignoreTransferOutAmount.toDouble() >= amount.toDouble() -> true
            else -> false
        }
    }


    private fun checkStopTime(stopTime: LocalDateTime?): Boolean {
        return stopTime?.isAfter(LocalDateTime.now()) ?: true
    }


    private fun getJoinPromotion(clientId: Int, platform: Platform, amount: BigDecimal, init: PlatformMemberTransferUo): PlatformMemberTransferUo {
        val promotions = promotionService.find(clientId = clientId, platform = platform)
                .filter { it.status == Status.Normal && this.checkStopTime(it.stopTime) }

        // 获得第一个符合优惠活动的
        val promotion = promotions.firstOrNull {
            when (it.ruleType) {
                PromotionRuleType.Bet -> {
                    val betRule = it.rule as PromotionRules.BetRule
                    betRule.minAmount.toDouble() <= amount.toDouble() && amount.toDouble() <= betRule.maxAmount.toDouble()
                }
                PromotionRuleType.Withdraw -> {
                    val withdrawRule = it.rule as PromotionRules.WithdrawRule
                    withdrawRule.minAmount.toDouble() <= amount.toDouble() && amount.toDouble() <= withdrawRule.maxAmount.toDouble()
                }
                else -> error(OnePieceExceptionCode.DATA_FAIL)
            }
        } ?: return init


        // 返回优惠活动Id和优惠金额
        return when (promotion.ruleType) {
            PromotionRuleType.Bet -> {
                val betRule = promotion.rule as PromotionRules.BetRule

                val promotionAmount = amount.multiply(betRule.promotionProportion)
                val requirementBet = (amount.plus(promotionAmount)).multiply(betRule.betMultiple)

                init.copy(joinPromotionId = promotion.id, currentBet = BigDecimal.ZERO, requirementBet = requirementBet, ignoreTransferOutAmount = betRule.ignoreTransferOutAmount,
                        promotionAmount = promotionAmount)
            }
            PromotionRuleType.Withdraw -> {
                val withdrawRule = promotion.rule as PromotionRules.WithdrawRule

                val promotionAmount = amount.multiply(withdrawRule.promotionProportion)
                val requirementTransferOutAmount = (amount.plus(promotionAmount)).multiply(withdrawRule.transferMultiplied)

                init.copy(joinPromotionId = promotion.id, currentBet = BigDecimal.ZERO, requirementTransferOutAmount = requirementTransferOutAmount, ignoreTransferOutAmount = withdrawRule.ignoreTransferOutAmount,
                        promotionAmount = promotionAmount)
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

    }


    private fun platformToCenterTransfer(clientId: Int, memberId: Int, platform: Platform, amount: BigDecimal) {
        val from = platform
        val to = Platform.Center

        // 获得平台用户信息
        val platformMemberVo = getPlatformMember(platform)
        val platformMember = platformMemberService.get(platformMemberVo.id)


        // 判断是否满足转出条件
        val state = this.checkTransferPlatformToCenter(amount = amount, platformMember = platformMember)
        check(state) { OnePieceExceptionCode.PLATFORM_TO_CENTER_FAIL }


        // 检查余额
        val wallet = walletService.getMemberWallet(memberId)
        check(wallet.balance.toDouble() - amount.toDouble() > 0) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }


        // 检查保证金是否足够
        platformBindService.updateEarnestBalance(clientId = clientId, platform = from, earnestBalance = amount)


        // 生成转账订单
        val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = platform)
        val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = amount, promotionAmount = BigDecimal.ZERO,
                from = from, to = to, joinPromotionId = null)
        transferOrderService.create(transferOrderCo)

        // 中心钱包加钱
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_IN, money = amount,
                remarks = "$platform => Center", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)


        //调用平台接口取款
        gameApi.transfer(clientId = clientId, platformUsername = platformMember.username, orderId = transferOrderId, amount = amount.negate(),
                platform = platform)

        // 更新转账订单
        val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = TransferState.Successful)
        transferOrderService.update(transferOrderUo)
    }


    private fun checkTransferPlatformToCenter(amount: BigDecimal, platformMember: PlatformMember): Boolean {

        // 1. 判断是否有优惠活动
        if (platformMember.joinPromotionId == null) return true

        // 2. 判断是否满足最小金额
        if (platformMember.ignoreTransferOutAmount.toDouble() >= amount.toDouble()) return true

        // 判断规则条件是否满足
        val promotion = promotionService.get(platformMember.joinPromotionId!!)
        return when (promotion.ruleType) {
            PromotionRuleType.Bet -> {
                platformMember.requirementBet.toDouble() >= platformMember.currentBet.toDouble()
            }
            PromotionRuleType.Withdraw -> {
                amount.toDouble() >= platformMember.requirementTransferOutAmount.toDouble()
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
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
    override fun balance(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestHeader("platform") platform: Platform
    ): BalanceVo {
        val member = current()
        return when (platform) {
            Platform.Center -> {
                val walletBalance = walletService.getMemberWallet(current().id).balance
                BalanceVo(platform = platform, balance = walletBalance, transfer = true, tips = null)
            }
            else -> {
                // 判断用户是否有参加活动
                val platformMemberVo = getPlatformMember(platform)
                val platformMember = platformMemberService.get(platformMemberVo.id)


                val platformBalance = gameApi.balance(clientId = member.clientId, platformUsername = platformMemberVo.platformUsername, platform = platform)

                var transfer = true
                var tips: String? = null
                if (platformMember.joinPromotionId != null) {
                    val promotion = promotionService.get(platformMember.joinPromotionId!!)

                    when (promotion.ruleType) {
                        PromotionRuleType.Bet -> {
                            transfer = platformMember.currentBet.toDouble() > platformMember.requirementBet.toDouble()
                            tips = "转出需要打码量:${platformMember.requirementBet}, 当前打码量:${platformMember.currentBet}"

                        }
                        PromotionRuleType.Withdraw -> {
                            transfer = platformMember.requirementTransferOutAmount.toDouble() <= platformBalance.toDouble()
                            tips = "转出需要最小金额:${platformMember.requirementTransferOutAmount.toDouble()}, 当前平台金额:${platformBalance.toDouble()}"
                        }
                        else -> error(OnePieceExceptionCode.DATA_FAIL)
                    }
                }
                BalanceVo(platform = platform, balance = platformBalance, transfer = transfer, tips = tips)
            }
        }
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





























