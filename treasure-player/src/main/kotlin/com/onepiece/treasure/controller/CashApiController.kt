package com.onepiece.treasure.controller

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.*
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.beans.value.internet.web.ClientBankVo
import com.onepiece.treasure.beans.value.internet.web.DepositVo
import com.onepiece.treasure.beans.value.internet.web.WithdrawVo
import com.onepiece.treasure.controller.basic.BasicController
import com.onepiece.treasure.controller.value.*
import com.onepiece.treasure.core.OrderIdBuilder
import com.onepiece.treasure.core.service.*
import com.onepiece.treasure.utils.AwsS3Util
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
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
        private val walletNoteService: WalletNoteService,
        private val promotionService: PromotionService,
        private val transferUtil: TransferUtil
) : BasicController(), CashApi {

    private val log = LoggerFactory.getLogger(CashApiController::class.java)


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

    @GetMapping("/checkBet")
    override fun checkBet(): CheckBetResp {
        val memberId = this.current().id

        val wallet = walletService.getMemberWallet(memberId)

        val platforms = platformMemberService.findPlatformMember(memberId)

        val kiss918Deposit = platforms.firstOrNull { it.platform == Platform.Kiss918 }?.totalTransferOutAmount?: BigDecimal.ZERO
        val pussyDeposit = platforms.find { it.platform == Platform.Pussy888 }?.totalTransferOutAmount?: BigDecimal.ZERO
        val megaDeposit = platforms.find { it.platform == Platform.Mega }?.totalTransferOutAmount?: BigDecimal.ZERO

        val betAmount = platforms.sumByDouble { it.totalBet.toDouble() }
        val needBet = wallet.totalDepositBalance.minus(kiss918Deposit).minus(pussyDeposit).minus(megaDeposit) * BigDecimal.valueOf(0.8)

        val overBet = betAmount.minus(needBet.toDouble()).toBigDecimal().setScale(2, 2)
        return CheckBetResp(currentBet = betAmount.toBigDecimal().setScale(2, 2), needBet = needBet, overBet = overBet)
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
                memberId = memberId, state = state, lockWaiterId = null, clientBankIdList = null)

        val page = depositService.query(depositQuery, current, size)
        if (page.total == 0) return Page.empty()

        val data = page.data.map {
            with(it) {
                DepositVo(orderId = it.orderId, money = money, state = it.state, remark = remarks, createdTime = createdTime,
                        endTime = endTime, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberName = memberName,
                        imgPath = imgPath, memberId = memberId, bankOrderId = null, clientBankCardNumber = clientBankCardNumber,
                        clientBankName = clientBankName, clientBankId = clientBankId, lockWaiterId = it.lockWaiterId, depositTime = it.depositTime,
                        channel = it.channel, username = username, clientBank = it.clientBank)
            }
        }

        return Page.of(page.total, data)
    }


    @PostMapping("/deposit")
    override fun deposit(@RequestBody depositCoReq: DepositCoReq): CashDepositResp {
        val clientBank = clientBankService.get(depositCoReq.clientBankId)
        val orderId = orderIdBuilder.generatorDepositOrderId()

        val current = this.current()

        val memberBankId = this.bindMemberBank(bankId = depositCoReq.memberBankId, bank = depositCoReq.memberBank, bankCardNumber = depositCoReq.memberBankCardNumber)

        val depositCo = DepositCo(orderId = orderId, memberId = current.id, memberName = current.name, memberBankCardNumber = depositCoReq.memberBankCardNumber,
                memberBank = depositCoReq.memberBank, clientId = current.clientId, clientBankId = clientBank.id, clientBankName = clientBank.name,
                clientBankCardNumber = clientBank.bankCardNumber, money = depositCoReq.money, imgPath = depositCoReq.imgPath, depositTime = depositCoReq.depositTime,
                channel = depositCoReq.channel, memberBankId = memberBankId, username = current.username, clientBank = clientBank.bank)
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
                memberId = memberId, state = state, lockWaiterId = null)

        val page = withdrawService.query(withdrawQuery, current, size)
        if (page.total == 0) return Page.empty()

        val data = page.data.map {
            with(it) {
                WithdrawVo(orderId = it.orderId, money = money, state = it.state, remark = remarks, createdTime = createdTime,
                        endTime = endTime, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberName = memberName,
                        memberId = it.memberId, memberBankId = it.memberBankId, lockWaiterId = it.lockWaiterId, username = username)
            }
        }

        return Page.of(page.total, data)
    }

    private fun bindMemberBank(bankId: Int?, bank: Bank?, bankCardNumber: String?): Int {

        val current = this.current()

        return if (bankId != null) {
            check(this.memberBankService.get(bankId).memberId == this.current().id) { OnePieceExceptionCode.AUTHORITY_FAIL }
            bankId
        } else {
            check(bank != null && bankCardNumber != null) { OnePieceExceptionCode.AUTHORITY_FAIL }
            val memberBankCo = MemberBankCo(clientId = current.clientId, memberId = current.id, bank = bank,
                    bankCardNumber = bankCardNumber)
            memberBankService.create(memberBankCo)
        }

    }

    @PostMapping("/withdraw")
    override fun withdraw(@RequestBody withdrawCoReq: WithdrawCoReq): CashWithdrawResp {

        val current = this.current()
        val clientId = current.clientId
        val memberId = current.id

        // 检查打码量
        check(this.checkBet().overBet.toDouble() <= 0) { "打码量不足" }

        val memberBankId = this.bindMemberBank(bankId = withdrawCoReq.memberBankId, bank = withdrawCoReq.bank, bankCardNumber = withdrawCoReq.bankCardNumber)

        // check bank id
        val memberBank = memberBankService.query(memberId).find { it.id == memberBankId }
        checkNotNull(memberBank) { OnePieceExceptionCode.AUTHORITY_FAIL }

        // check safety password
//        memberService.checkSafetyPassword(id = memberId, safetyPassword = withdrawCoReq.safetyPassword)

        // create order
        val orderId = orderIdBuilder.generatorWithdrawOrderId()
        val withdrawCo = WithdrawCo(orderId = orderId, clientId = clientId, memberId = memberId,
                memberBank = memberBank.bank, memberBankCardNumber = memberBank.bankCardNumber, memberBankId = memberBank.id,
                money = withdrawCoReq.money, remarks = null, username = current.username)
        withdrawService.create(withdrawCo)

        return CashWithdrawResp(orderId = orderId)
    }

    @GetMapping("/check/promotion")
    override fun checkPromotion(
            @RequestHeader("language") language: Language,
            @RequestParam("platform") platform: Platform,
            @RequestParam("amount") amount: BigDecimal,
            @RequestParam("promotionId", required = false) promotionId: Int?
    ): CheckPromotinResp {

        val member = this.current()

        val wallet = walletService.getMemberWallet(memberId = member.id)
        val promotions = promotionService.find(clientId = member.clientId, platform = platform)

        val joinPromotions = promotions
                .filter { promotionId == null || it.id == promotionId }
                .filter { it.rule.minAmount.toDouble() <= amount.toDouble() && amount.toDouble() <= it.rule.maxAmount.toDouble() }
                .filter { wallet.totalTransferOutFrequency == 0 || it.category != PromotionCategory.First }


        val checkPromotions = joinPromotions.map { promotion ->

            val platformMemberVo = getPlatformMember(platform)
            val platformBalance = gameApi.balance(clientId = member.clientId, platform = platform, platformUsername = platformMemberVo.platformUsername,
                    platformPassword =  platformMemberVo.platformPassword)
            val promotionIntroduction = promotion.getPromotionIntroduction(amount = amount, language = language, platformBalance = platformBalance)
            CheckPromotionVo(promotionId = promotion.id, promotionIntroduction = promotionIntroduction)
        }

        return CheckPromotinResp(promotions = checkPromotions)
    }

    @PutMapping("/transfer")
    @Transactional(rollbackFor = [Exception::class])
    override fun transfer(@RequestBody cashTransferReq: CashTransferReq) {
        check(cashTransferReq.from != cashTransferReq.to) { OnePieceExceptionCode.AUTHORITY_FAIL }
        check(cashTransferReq.amount.toDouble() > 0 || cashTransferReq.amount.toInt() == -1) { OnePieceExceptionCode.ILLEGAL_OPERATION }

        val current = this.current()

        if (cashTransferReq.from != Platform.Center) {
            val platformMemberVo = getPlatformMember(platform = cashTransferReq.from)
            val toCenterTransferReq = cashTransferReq.copy(to = Platform.Center)
            transferUtil.transfer(clientId = current.clientId, platformMemberVo = platformMemberVo, cashTransferReq = toCenterTransferReq)

        }

        if (cashTransferReq.to != Platform.Center) {
            val toPlatformTransferReq = cashTransferReq.copy(from = Platform.Center)
            val platformMemberVo = getPlatformMember(platform = cashTransferReq.to)
            transferUtil.transfer(clientId = current.clientId, platformMemberVo = platformMemberVo, cashTransferReq = toPlatformTransferReq)
        }
    }

    @PutMapping("/transfer/in/all")
    override fun transferToCenter(): List<BalanceAllInVo> {
        val current = this.current()
        return transferUtil.transferInAll(clientId = current.clientId, memberId = current.id, exceptPlatform = null)
    }


    @GetMapping("/wallet/note")
    override fun walletNote(
            @RequestParam(value = "onlyPromotion", defaultValue = "false") onlyPromotion: Boolean,
            @RequestParam(value = "events", required = false) events: String?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): List<WalletNoteVo> {
        val member = this.current()

        val eventList = when (onlyPromotion) {
            true -> listOf(WalletEvent.TRANSFER_OUT)
            false -> events?.let { it.split(",").map { WalletEvent.valueOf(it) } }
        }

        val walletNoteQuery = WalletNoteQuery(clientId = member.clientId, memberId = member.id, current = current, size = size, event = null, events = eventList, onlyPromotion = onlyPromotion)
        val list = walletNoteService.query(walletNoteQuery)
        return list.map {
            WalletNoteVo(id = it.id, memberId = it.memberId, eventId = it.eventId, event = it.event, money = it.money, remarks = it.remarks, createdTime = it.createdTime,
                    promotionMoney = it.promotionMoney)
        }
    }

    @GetMapping("/balance")
    override fun balance(
            @RequestHeader("language") language: Language,
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
            @RequestHeader("language") language: Language,
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
                    val platformBalance = try {
                        gameApi.balance(clientId = clientId, platformUsername = platformMember.username, platform = it.platform,
                                platformPassword = platformMember.password)
                    } catch (e: Exception) {
                        BigDecimal.valueOf(-1)
                    }

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
























