package com.onepiece.treasure.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.model.Promotion
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.beans.value.internet.web.ClientBankVo
import com.onepiece.treasure.beans.value.internet.web.DepositVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawVo
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import com.onepiece.treasure.core.OrderIdBuilder
import com.onepiece.treasure.core.service.*
import com.onepiece.treasure.utils.AwsS3Util
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.stream.Collectors

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
    override fun banks(): List<BankVo> {
        return Bank.values().map { BankVo(logo = it.logo, name = it.cname) }
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
                    MemberBankVo(id = myBank.id, name = member.name, bank = myBank.bank, bankCardNumber = myBank.bankCardNumber,
                            clientId = member.clientId, memberId = member.id, logo = myBank.bank.logo)
                }
                else -> {
                    MemberBankVo(id = -1, name = it.cname, bank = it, bankCardNumber = null, clientId = member.clientId,
                            memberId = member.clientId, logo = it.logo)
                }
            }
        }
    }

    @PostMapping("/bank/my")
    override fun bankCreate(@RequestBody memberBankCoReq: MemberBankCoReq) {

        val (clientId, memberId) = this.currentClientIdAndMemberId()
        val memberBankCo = MemberBankCo(clientId = clientId, memberId = memberId, bank = memberBankCoReq.bank,
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

        val member = memberService.getMember(current().id)

        return clientBankService.findClientBank(current().clientId)
                .filter {
                    it.status == Status.Normal &&
                            (it.levelId == null || it.levelId == 0 || it.levelId == member.levelId) }
                .map {
                    with(it) {
                        ClientBankVo(id = id, bank = bank, bankName = bank.cname, name = name, bankCardNumber = bankCardNumber,
                                status = status, createdTime = createdTime, levelId = null, levelName = null, logo = bank.logo)
                    }
                }
    }

    @PostMapping("/upload/proof")
    override fun uploadProof(@RequestParam("file") file: MultipartFile): Map<String, String> {
        val url = AwsS3Util.upload(file = file, clientId = current().clientId, category = "bank_proof")
        return mapOf(
                "path" to url
        )
    }

    @GetMapping("/deposit")
    override fun deposit(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<DepositVo> {

        val (clientId, memberId) = this.currentClientIdAndMemberId()

        val depositQuery = DepositQuery(clientId = clientId, startTime = null, endTime = null, orderId = orderId,
                memberId = memberId, state = state)

        val page = depositService.query(depositQuery, current, size)
        if (page.total == 0) return Page.empty()

        val data = page.data.map {
            with(it) {
                DepositVo(orderId = it.orderId, money = money, state = it.state, remark = remarks, createdTime = createdTime,
                        endTime = endTime, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberName = memberName,
                        imgPath = imgPath, memberId = memberId, bankOrderId = null, clientBankCardNumber = clientBankCardNumber,
                        clientBankName = clientBankName, clientBankId = clientBankId, lockWaiterId = it.lockWaiterId)
            }
        }

        return Page.of(page.total, data)
    }


    @PostMapping("/deposit")
    override fun deposit(@RequestBody depositCoReq: DepositCoReq): CashDepositResp {

        val clientBank = clientBankService.get(depositCoReq.clientBankId)
        val orderId = orderIdBuilder.generatorDepositOrderId()

        val current = this.current()

        val depositCo = DepositCo(orderId = orderId, memberId = current.id, memberName = current.name, memberBankCardNumber = depositCoReq.memberBankCardNumber,
                memberBank = depositCoReq.memberBank, clientId = current.clientId, clientBankId = clientBank.id, clientBankName = clientBank.name,
                clientBankCardNumber = clientBank.bankCardNumber, money = depositCoReq.money, imgPath = depositCoReq.imgPath)
        depositService.create(depositCo)

        return CashDepositResp(orderId = orderId)
    }

    @GetMapping("/withdraw")
    override fun withdraw(
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WithdrawVo> {


        val (clientId, memberId) = this.currentClientIdAndMemberId()

        val withdrawQuery = WithdrawQuery(clientId = clientId, startTime = null, endTime = null, orderId = orderId,
                memberId = memberId, state = state)

        val page = withdrawService.query(withdrawQuery, current, size)
        if (page.total == 0) return Page.empty()

        val data = page.data.map {
            with(it) {
                WithdrawVo(orderId = it.orderId, money = money, state = it.state, remark = remarks, createdTime = createdTime,
                        endTime = endTime, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberName = memberName,
                        memberId = it.memberId, memberBankId = it.memberBankId, lockWaiterId = it.lockWaiterId)
            }
        }

        return Page.of(page.total, data)
    }

    @PostMapping("/withdraw")
    override fun withdraw(@RequestBody withdrawCoReq: WithdrawCoReq): CashWithdrawResp {

        val (clientId, memberId) = currentClientIdAndMemberId()

        // 获得用户银行卡Id
        val memberBankId = if (withdrawCoReq.memberBankId == null) {
            val memberBankCo = MemberBankCo(clientId = clientId, memberId = memberId, bank = withdrawCoReq.bank,
                    bankCardNumber = withdrawCoReq.bankCardNumber)
            memberBankService.create(memberBankCo)
        } else withdrawCoReq.memberBankId

        // check bank id
        val memberBank = memberBankService.query(memberId).find { it.id == memberBankId }
        checkNotNull(memberBank) { OnePieceExceptionCode.AUTHORITY_FAIL }

        // check safety password
//        memberService.checkSafetyPassword(id = memberId, safetyPassword = withdrawCoReq.safetyPassword)

        // create order
        val orderId = orderIdBuilder.generatorWithdrawOrderId()
        val withdrawCo = WithdrawCo(orderId = orderId, clientId = clientId, memberId = memberId,
                memberBank = memberBank.bank, memberBankCardNumber = memberBank.bankCardNumber, memberBankId = memberBank.id,
                money = withdrawCoReq.money, remarks = null)
        withdrawService.create(withdrawCo)

        return CashWithdrawResp(orderId = orderId)
    }


    @GetMapping("/check/promotion")
    override fun checkPromotion(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestParam("platform") platform: Platform,
            @RequestParam("amount") amount: BigDecimal,
            @RequestParam("promotionId", required = false) promotionId: Int?
    ): CheckPromotionVo {

        val member = this.current()
        val promotions = promotionService.find(clientId = member.clientId, platform = platform)

        val promotion = promotions.firstOrNull {
            (promotionId != null && it.id == promotionId) ||
                    (it.rule.minAmount.toDouble() <= amount.toDouble() && amount.toDouble() <= it.rule.maxAmount.toDouble())
        }

        val platformMemberVo = getPlatformMember(platform)
        val platformBalance = gameApi.balance(clientId = member.clientId, platform = platform, platformUsername = platformMemberVo.platformUsername,
                platformPassword =  platformMemberVo.platformPassword)
        val promotionIntroduction = promotion?.getPromotionIntroduction(amount = amount, language = language, platformBalance = platformBalance)
        return CheckPromotionVo(promotion = promotion != null, promotionId = promotion?.id, promotionIntroduction = promotionIntroduction)
    }

    @PutMapping("/transfer")
    @Transactional(rollbackFor = [Exception::class])
    override fun transfer(@RequestBody cashTransferReq: CashTransferReq) {
        check(cashTransferReq.from != cashTransferReq.to) { OnePieceExceptionCode.AUTHORITY_FAIL }

        val current = this.current()

        if (cashTransferReq.from != Platform.Center) {
            this.singleTransfer(clientId = current.clientId, platform = cashTransferReq.from, cashTransferReq = cashTransferReq, type = "in")
        }

        if (cashTransferReq.to != Platform.Center) {
            this.singleTransfer(clientId = current.clientId, platform = cashTransferReq.to, cashTransferReq = cashTransferReq, type = "out")
        }
    }


    private fun singleTransfer(clientId: Int, platform: Platform, cashTransferReq: CashTransferReq, type: String) {

        val platformMemberVo = this.getPlatformMember(platform)
        val platformMember = platformMemberService.get(platformMemberVo.id)

        val platformBalance  = gameApi.balance(clientId = clientId, platformUsername = platformMemberVo.platformUsername, platform = platform, platformPassword = platformMember.password)

        if (type == "out") { // 中心钱包 -> 平台钱包
            this.centerToPlatformTransfer(platformMember = platformMember, platformBalance = platformBalance, amount = cashTransferReq.amount, promotionId = cashTransferReq.promotionId)
        } else if (type == "in") { // 平台钱包 -> 中心钱包
            this.platformToCenterTransfer(platformMember = platformMember, platformBalance = platformBalance, amount = cashTransferReq.amount)
        }

//        when (cashTransferReq.from) {
//            /
//            Platform.Center -> {
//                this.centerToPlatformTransfer(platformMember = platformMember, platformBalance = platformBalance, amount = cashTransferReq.amount, promotionId = cashTransferReq.promotionId)
//            }
//
//            else -> {
//                this.platformToCenterTransfer(platformMember = platformMember, platformBalance = platformBalance, amount = cashTransferReq.amount)
//            }
//        }
    }

    // 中心转平台
    private fun centerToPlatformTransfer(platformMember: PlatformMember, platformBalance: BigDecimal,  amount: BigDecimal, promotionId: Int?) {

        val platform = platformMember.platform
        val from = Platform.Center
        val to = platformMember.platform
        val clientId = platformMember.clientId
        val memberId = platformMember.memberId

        // 检查余额
        val wallet = walletService.getMemberWallet(platformMember.memberId)
        check(wallet.balance.toDouble() - amount.toDouble() >= 0) { OnePieceExceptionCode.BALANCE_SHORT_FAIL }

        // 优惠活动赠送金额
        val platformMemberTransferUo = this.handlerPromotion(platformMember = platformMember, amount = amount, promotionId = promotionId, platformBalance = platformBalance)

        // 检查保证金是否足够
        platformBindService.updateEarnestBalance(clientId = clientId, platform = platform, earnestBalance = amount.negate())

        // 转账订单编号
        val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = platform, transfer = "out")

        // 中心钱包扣款
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_OUT, money = amount,
                remarks = "Center => $platform", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)

        // 生成转账订单
        val promotionAmount = platformMemberTransferUo?.promotionAmount?: BigDecimal.ZERO
        val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = amount, promotionAmount = promotionAmount,
                from = from, to = to, joinPromotionId = platformMemberTransferUo?.joinPromotionId)
        transferOrderService.create(transferOrderCo)

        // 平台钱包更改信息
        if (platformMemberTransferUo != null) {
            platformMemberService.transferIn(platformMemberTransferUo)
        } else {
            val init = PlatformMemberTransferUo(id = platformMember.id, joinPromotionId = null, currentBet = BigDecimal.ZERO, requirementBet = BigDecimal.ZERO,
                    promotionAmount = BigDecimal.ZERO, transferAmount = amount, requirementTransferOutAmount = BigDecimal.ZERO, ignoreTransferOutAmount = BigDecimal.ZERO)
            platformMemberService.transferIn(init)
        }

        //调用平台接口充值
        val transferFlag = gameApi.transfer(clientId = clientId, platformUsername = platformMember.username, orderId = transferOrderId, amount = amount.plus(promotionAmount),
                platform = to)

        // 转账失败 进行回滚
        if (!transferFlag) {
            this.transferRollBack(clientId = clientId, memberId = memberId, money = amount, from = from, to = to, transferOrderId = transferOrderId)
        }

        // 更新转账订单
        val state = if (transferFlag) TransferState.Successful else TransferState.Fail
        val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = state)
        transferOrderService.update(transferOrderUo)
    }

    private fun transferRollBack(clientId: Int, memberId: Int, money: BigDecimal, from: Platform, to: Platform, transferOrderId: String) {

        val event = if (from == Platform.Center) WalletEvent.TRANSFER_OUT_ROLLBACK else WalletEvent.TRANSFER_IN_ROLLBACK
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = event, money = money,
                remarks = "$from => $to", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)

    }

    /**
     * 处理优惠活动
     */
    private fun handlerPromotion(platformMember: PlatformMember, platformBalance: BigDecimal, amount: BigDecimal, promotionId: Int?): PlatformMemberTransferUo? {

        // 是否有历史优惠活动
        if (platformMember.joinPromotionId != null) {
            // 已存在的优惠活动
            val promotion = promotionService.get(platformMember.joinPromotionId!!)

            // 是否满足清空优惠活动
            val cleanState = this.checkCleanPromotion(promotion = promotion, platformBalance = platformBalance, platformMember = platformMember)
            check(cleanState) { OnePieceExceptionCode.PLATFORM_HAS_BALANCE_PROMOTION_FAIL }
        }

        if (promotionId == null) return null

        // 获得新的优惠活动信息
        val promotion = promotionService.get(promotionId)
        check(promotion.status == Status.Normal) { OnePieceExceptionCode.PROMOTION_EXPIRED }
        check(this.checkStopTime(promotion.stopTime)) { OnePieceExceptionCode.PROMOTION_EXPIRED }

        return promotion.getPlatformMemberTransferUo(platformMemberId = platformMember.id, amount =  amount, platformBalance = platformBalance, promotionId = promotionId)
    }


    /**
     * 检查是否清空优惠活动信息
     */
    fun checkCleanPromotion(promotion: Promotion, platformMember: PlatformMember, platformBalance: BigDecimal): Boolean {

        val state = when{
            platformBalance.toDouble() <= promotion.rule.ignoreTransferOutAmount.toDouble() -> true
            promotion.ruleType == PromotionRuleType.Bet -> {
                platformMember.currentBet.toDouble() >= platformMember.requirementBet.toDouble()
            }
            promotion.ruleType == PromotionRuleType.Withdraw -> {
                platformBalance.toDouble() >= platformMember.requirementTransferOutAmount.toDouble()
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }


        if (state) {
            platformMemberService.cleanTransferIn(memberId = platformMember.memberId, platform = platformMember.platform)
        }

        return state
    }


    private fun checkStopTime(stopTime: LocalDateTime?): Boolean {
        return stopTime?.isAfter(LocalDateTime.now()) ?: true
    }


    private fun platformToCenterTransfer(platformMember: PlatformMember, platformBalance: BigDecimal, amount: BigDecimal) {
        val platform = platformMember.platform
        val from = platformMember.platform
        val to = Platform.Center
        val clientId = platformMember.clientId
        val memberId = platformMember.memberId


        // 判断是否满足转出条件
        if (platformMember.joinPromotionId != null) {
            val promotion = promotionService.get(platformMember.joinPromotionId!!)
            val checkState = this.checkCleanPromotion(promotion = promotion, platformMember = platformMember, platformBalance = platformBalance)
            check(checkState) { OnePieceExceptionCode.PLATFORM_TO_CENTER_FAIL }
        }


        // 检查保证金是否足够
        platformBindService.updateEarnestBalance(clientId = clientId, platform = from, earnestBalance = amount)


        // 生成转账订单
        val transferOrderId = orderIdBuilder.generatorTransferOrderId(clientId = clientId, platform = platform, transfer = "in")
        val transferOrderCo = TransferOrderCo(orderId = transferOrderId, clientId = clientId, memberId = memberId, money = amount, promotionAmount = BigDecimal.ZERO,
                from = from, to = to, joinPromotionId = null)
        transferOrderService.create(transferOrderCo)

        // 中心钱包加钱
        val walletUo = WalletUo(clientId = clientId, memberId = memberId, event = WalletEvent.TRANSFER_IN, money = amount,
                remarks = "$platform => Center", waiterId = null, eventId = transferOrderId)
        walletService.update(walletUo)


        //调用平台接口取款
        val transferFlag = gameApi.transfer(clientId = clientId, platformUsername = platformMember.username, orderId = transferOrderId, amount = amount.negate(),
                platform = platform)

        if (!transferFlag) {
            this.transferRollBack(clientId = clientId, memberId = memberId, money = amount, from = from, to = to, transferOrderId = transferOrderId)
        }

        // 更新转账订单
        val transferState = if (transferFlag) TransferState.Successful else TransferState.Fail
        val transferOrderUo = TransferOrderUo(orderId = transferOrderId, state = transferState)
        transferOrderService.update(transferOrderUo)

        // 清空平台用户优惠信息
        if (transferFlag)
            platformMemberService.cleanTransferIn(memberId = memberId, platform = platform, transferOutAmount = amount)
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

        val walletBalance = walletService.getMemberWallet(current().id).balance

        return when (platform) {
            Platform.Center -> {
                BalanceVo(platform = platform, balance = walletBalance, transfer = true, tips= null, centerBalance = walletBalance)
            }
            else -> {
                // 判断用户是否有参加活动
                val platformMemberVo = getPlatformMember(platform)
                val platformMember = platformMemberService.get(platformMemberVo.id)

                val platformBalance = gameApi.balance(clientId = member.clientId, platformUsername = platformMemberVo.platformUsername, platform = platform,
                        platformPassword = platformMember.password)
                val (transfer, tips) = this.checkCanTransferOutAndTips(platformMember = platformMember, platformBalance = platformBalance, language = language)


//                val transferIn = platformMember.joinPromotionId?.let {
//                    val promotion = promotionService.get(it)
//                    this.checkCleanPromotion(promotion = promotion, platformBalance = platformBalance, platformMember = platformMember)
//                }?: true

                BalanceVo(platform = platform, balance = platformBalance, transfer = transfer, tips = tips, centerBalance = walletBalance)
            }
        }
    }

    @GetMapping("/balances")
    override fun balances(
            @RequestHeader("language", defaultValue = "EN") language: Language,
            @RequestParam("category", required = false) category: PlatformCategory?
    ): List<BalanceVo> {

        val member = this.current()
        val clientId = member.clientId
        val memberId = member.id

        // 查询主钱包
        val wallet = walletService.getMemberWallet(memberId = memberId)
        val walletBalanceVo = BalanceVo(platform = Platform.Center, balance = wallet.balance, transfer = true, tips = null, centerBalance = wallet.balance)

        // 查询厅主开通的平台列表
        val platforms = platformBindService.findClientPlatforms(clientId)

        // 查询用户开通的平台列表
        val platformMemberMap = platformMemberService.findPlatformMember(memberId = memberId).map { it.platform to it }.toMap()


        // 查询余额 //TODO 暂时用简单的异步去处理
        val balances = platforms.filter { category == null || it.platform.detail.category == category }.parallelStream().map {
            val platformMember = platformMemberMap[it.platform]

            when (platformMember == null) {
                true -> BalanceVo(platform = it.platform, balance = BigDecimal.ZERO, transfer = true, tips = null, centerBalance = wallet.balance)
                else -> {
                    val platformBalance = gameApi.balance(clientId = clientId, platformUsername = platformMember.username, platform = it.platform,
                            platformPassword = platformMember.password)

//                    val transferIn = platformMember.joinPromotionId?.let {
//                        val promotion = promotionService.get(it)
//                        this.checkCleanPromotion(promotion = promotion, platformBalance = platformBalance, platformMember = platformMember)
//                    }?: true

                    val (transfer, tips) = this.checkCanTransferOutAndTips(platformMember = platformMember, platformBalance = platformBalance, language = language)
                    BalanceVo(platform = it.platform, balance = platformBalance, transfer = transfer, tips = tips, centerBalance = wallet.balance)
                }
            }
        }.collect(Collectors.toList())

        return balances.plus(walletBalanceVo)
    }


    private fun checkCanTransferOutAndTips(platformMember: PlatformMember, platformBalance: BigDecimal, language: Language): Pair<Boolean, String?> {

        if (platformMember.joinPromotionId == null) return true to null

        val promotion = promotionService.get(platformMember.joinPromotionId!!)
        return when {
            promotion.rule.ignoreTransferOutAmount.toDouble() >= platformBalance.toDouble() -> true to null
            promotion.ruleType == PromotionRuleType.Bet -> {
                val transfer = platformMember.currentBet.toDouble() > platformMember.requirementBet.toDouble()
                val tips = "转出需要打码量:${platformMember.requirementBet}, 当前打码量:${platformMember.currentBet}"
                transfer to tips
            }
            promotion.ruleType == PromotionRuleType.Withdraw -> {
                val transfer = platformMember.requirementTransferOutAmount.toDouble() <= platformBalance.toDouble()
                val tips = "转出需要最小金额:${platformMember.requirementTransferOutAmount.toDouble()}, 当前平台金额:${platformBalance.toDouble()}"
                transfer to tips
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

}





























