package com.onepiece.gpgaming.player.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.DepositState
import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.PayState
import com.onepiece.gpgaming.beans.enums.PayType
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.PromotionCategory
import com.onepiece.gpgaming.beans.enums.PromotionPeriod
import com.onepiece.gpgaming.beans.enums.PromotionRuleType
import com.onepiece.gpgaming.beans.enums.RiskLevel
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.enums.WithdrawState
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.I18nContent
import com.onepiece.gpgaming.beans.model.PlatformMember
import com.onepiece.gpgaming.beans.model.pay.InstantPayConfig
import com.onepiece.gpgaming.beans.model.pay.MaxiPayConfig
import com.onepiece.gpgaming.beans.model.pay.SurePayConfig
import com.onepiece.gpgaming.beans.value.database.DepositCo
import com.onepiece.gpgaming.beans.value.database.DepositQuery
import com.onepiece.gpgaming.beans.value.database.MemberBankCo
import com.onepiece.gpgaming.beans.value.database.MemberBankUo
import com.onepiece.gpgaming.beans.value.database.MemberIntroduceValue
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.PayOrderValue
import com.onepiece.gpgaming.beans.value.database.WalletNoteQuery
import com.onepiece.gpgaming.beans.value.database.WithdrawCo
import com.onepiece.gpgaming.beans.value.database.WithdrawQuery
import com.onepiece.gpgaming.beans.value.internet.web.BankVo
import com.onepiece.gpgaming.beans.value.internet.web.CashValue
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankVo
import com.onepiece.gpgaming.beans.value.internet.web.DepositValue
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberVo
import com.onepiece.gpgaming.beans.value.internet.web.SelectPayVo
import com.onepiece.gpgaming.beans.value.internet.web.ThirdPayValue
import com.onepiece.gpgaming.beans.value.internet.web.WithdrawValue
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.core.service.ClientBankService
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.core.service.DepositService
import com.onepiece.gpgaming.core.service.I18nContentService
import com.onepiece.gpgaming.core.service.MemberBankService
import com.onepiece.gpgaming.core.service.MemberDailyReportService
import com.onepiece.gpgaming.core.service.MemberIntroduceService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PayBindService
import com.onepiece.gpgaming.core.service.PayOrderService
import com.onepiece.gpgaming.core.service.PromotionService
import com.onepiece.gpgaming.core.service.TransferOrderService
import com.onepiece.gpgaming.core.service.WalletNoteService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.core.service.WithdrawService
import com.onepiece.gpgaming.core.utils.OrderIdBuilder
import com.onepiece.gpgaming.payment.PayGateway
import com.onepiece.gpgaming.payment.PayRequest
import com.onepiece.gpgaming.player.controller.basic.BasicController
import com.onepiece.gpgaming.player.controller.value.BalanceVo
import com.onepiece.gpgaming.player.controller.value.CashDepositResp
import com.onepiece.gpgaming.player.controller.value.CashWithdrawResp
import com.onepiece.gpgaming.player.controller.value.CheckBankResp
import com.onepiece.gpgaming.player.controller.value.CheckBetResp
import com.onepiece.gpgaming.player.controller.value.CheckPromotinResp
import com.onepiece.gpgaming.player.controller.value.CheckPromotionVo
import com.onepiece.gpgaming.player.controller.value.DepositCoReq
import com.onepiece.gpgaming.player.controller.value.MemberBankCoReq
import com.onepiece.gpgaming.player.controller.value.MemberBankUoReq
import com.onepiece.gpgaming.player.controller.value.MemberBankVo
import com.onepiece.gpgaming.player.controller.value.MemberDailyReportValue
import com.onepiece.gpgaming.player.controller.value.WalletNoteVo
import com.onepiece.gpgaming.player.controller.value.WithdrawCoReq
import com.onepiece.gpgaming.player.jwt.JwtUser
import com.onepiece.gpgaming.utils.AwsS3Util
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.stream.Collectors
import kotlin.streams.toList

@Suppress("CAST_NEVER_SUCCEEDS")
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
        private val transferUtil: TransferUtil,
        private val i18nContentService: I18nContentService,
        private val objectMapper: ObjectMapper,
        private val transferOrderService: TransferOrderService,
        private val payBindService: PayBindService,
        private val payOrderService: PayOrderService,
        private val payGateway: PayGateway,
        private val memberDailyReportService: MemberDailyReportService,
        private val betOrderService: BetOrderService,
        private val memberIntroduceService: MemberIntroduceService,
        private val clientConfigService: ClientConfigService
) : BasicController(), CashApi {

    private val log = LoggerFactory.getLogger(CashApiController::class.java)


    @GetMapping("/bank")
    override fun banks(): List<BankVo> {

        val launch = getHeaderLaunch()

        val selectCountry = this.getWebSite()
                .let {
                    when {
                        it.country == Country.Default && it.bossId == -1 -> {
                            null
//                            clientService.getMainClient(bossId = it.clientId)?.country ?: Country.Malaysia
                        }
                        it.country == Country.Default -> {
                            clientService.getMainClient(bossId = it.bossId)?.country ?: Country.Malaysia
                        }
                        else -> it.country
                    }
                }

        val mobile = launch == LaunchMethod.Wap

        return Country.values().filter { it == selectCountry || selectCountry == null }.map { country ->
            val banks = Bank.of(country = country)

            banks.map {
                BankVo(
                        country = country,
                        grayLogo = if (mobile) it.grayLogo else it.mGrayLogo,
                        logo = if (mobile) it.mLogo else it.logo,
                        name = it.cname,
                        bank = it
                )
            }
        }.reduce { acc, list -> acc.plus(list) }
    }

    @GetMapping("/bank/my")
    override fun myBanks(): List<MemberBankVo> {
        val member = this.current()

        // 我的银行卡列表
        val myBankMap = memberBankService.query(member.id).map { it.bank to it }.toMap()

        return this.banks().map {
            val myBank = myBankMap[it.bank]

            when (myBank != null) {
                true -> {
                    MemberBankVo(id = myBank.id, name = member.name, bank = myBank.bank, bankCardNumber = myBank.bankCardNumber,
                            clientId = member.clientId, memberId = member.id, logo = myBank.bank.logo, grayLogo = myBank.bank.grayLogo)
                }
                else -> {
                    MemberBankVo(id = -1, name = it.name, bank = it.bank, bankCardNumber = null, clientId = member.clientId,
                            memberId = member.clientId, logo = it.logo, grayLogo = it.grayLogo)
                }
            }
        }
    }

    @GetMapping("/bank/check")
    override fun checkBank(@RequestParam("bankCardNo") bankCardNo: String): CheckBankResp {
        val member = this.current()
        val bank = memberBankService.exist(clientId = member.clientId, bankNo = bankCardNo)
        return CheckBankResp(bank != null)
    }

    @GetMapping("/checkBet")
    override fun checkBet(): CheckBetResp {
        val user = this.current()
        val memberId = user.id

        val wallet = walletService.getMemberWallet(memberId)

        val platforms = platformMemberService.findPlatformMember(memberId)

        val kiss918Deposit = platforms.firstOrNull { it.platform == Platform.Kiss918 }?.totalAmount ?: BigDecimal.ZERO
        val pussyDeposit = platforms.find { it.platform == Platform.Pussy888 }?.totalAmount ?: BigDecimal.ZERO
        val megaDeposit = platforms.find { it.platform == Platform.Mega }?.totalAmount ?: BigDecimal.ZERO

        val currentBet = platforms.sumByDouble { it.totalBet.toDouble() }
                .let { BigDecimal.valueOf(it) }
                .plus(kiss918Deposit)
                .plus(pussyDeposit)
                .plus(megaDeposit)
                .setScale(2, 2)

        val needBet = wallet.totalDepositBalance.multiply(BigDecimal.valueOf(0.8))
                .setScale(2, 2)

//        val overBet = betAmount.minus(needBet.toDouble()).toBigDecimal().setScale(2, 2)
        val overBet = needBet.minus(currentBet)

        log.info("用户:${memberId}, 检查打码量，当前打码量：$currentBet, 需要打码量:$needBet, 剩余打码量：$overBet")


        val now = LocalDateTime.now()
        val startDate = when {
            now.hour < 5 -> LocalDate.now().minusDays(1)
            else -> LocalDate.now()
        }


        // 检查返水必需 < 打码量
        val today = LocalDate.now()
        val memberDailyReportQuery = MemberReportQuery(clientId = user.clientId, memberId = user.id, startDate = today.minusDays(1),
                agentId = null, endDate = today, current = 0, size = 1, minRebateAmount = null, minPromotionAmount = null)
        val rebate = memberDailyReportService.query(memberDailyReportQuery).firstOrNull()?.rebateAmount ?: BigDecimal.ZERO
        val todayBet = betOrderService.getTotalBet(clientId = user.clientId, memberId = user.id, startDate = startDate)
        val todayWithdraw = withdrawService.getTotalWithdraw(clientId = user.clientId, memberId = user.id, startDate = startDate)
        val lastWithdraw = wallet.balance.minus(todayWithdraw).minus(rebate)


        val clientConfig = clientConfigService.get(clientId = user.clientId)
        return CheckBetResp(currentBet = currentBet, needBet = needBet, overBet = overBet, yesRebate = rebate, todayBet = todayBet, lastWithdraw = lastWithdraw,
                totalDeposit = wallet.totalDepositBalance, minWithdrawRequire = clientConfig.minWithdrawRequire)
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

    @GetMapping("/pays")
    override fun payList(): SelectPayVo {

        val banks = this.clientBanks()
        val thirdPays = this.thirdPay()

        return SelectPayVo(banks = banks, thirdPays = thirdPays)
    }

    @GetMapping("/bank/client")
    override fun clientBanks(): List<ClientBankVo> {

        val member = memberService.getMember(current().id)

        return clientBankService.findClientBank(current().clientId)
                .filter {
                    it.status == Status.Normal &&
                            (it.levelId == null || it.levelId == 0 || it.levelId == member.levelId)
                }
                .map {
                    with(it) {
                        ClientBankVo(id = id, bank = bank, bankName = bank.cname, name = name, bankCardNumber = bankCardNumber,
                                status = status, createdTime = createdTime, levelId = null, levelName = null, logo = bank.logo,
                                grayLogo = bank.grayLogo, minAmount = minAmount, maxAmount = maxAmount)
                    }
                }
    }

    @GetMapping("/thirdpay/pay")
    override fun thirdPay(): List<ThirdPayValue.SupportPay> {

        val current = this.current()
        val member = memberService.getMember(current.id)

        return payBindService.list(clientId = member.clientId).filter { it.levelId == null || it.levelId == member.levelId }
                .map {

                    // 支持的银行列表
                    val banks = when (it.payType) {
                        PayType.SurePay -> {
                            val config = it.getConfig(objectMapper) as SurePayConfig

                            config.supportBanks.map { sb ->
                                val bank = sb.bank
                                BankVo(bank = bank, name = bank.cname, logo = bank.logo, grayLogo = bank.grayLogo, country = Country.Default)
                            }
                        }
                        PayType.MaxiPay -> {
                            val config = it.getConfig(objectMapper) as MaxiPayConfig

                            config.supportBanks.map { bank ->
                                BankVo(bank = bank, name = bank.cname, logo = bank.logo, grayLogo = bank.grayLogo, country = Country.Default)
                            }
                        }
                        PayType.InstantPay -> {
                            val config = it.getConfig(objectMapper) as InstantPayConfig

                            config.supportBanks.map { bank ->
                                BankVo(bank = bank, name = bank.cname, logo = bank.logo, grayLogo = bank.grayLogo, country = Country.Default)
                            }
                        }
                        else -> null
                    }
                    ThirdPayValue.SupportPay(payId = it.id, payType = it.payType, minAmount = it.minAmount, maxAmount = it.maxAmount,
                            banks = banks)
                }.sortedBy { it.payType.sort }
    }

    @PostMapping("/thirdpay/select")
    override fun selectPay(
            @RequestParam("payId") payId: Int,
            @RequestParam("amount") amount: BigDecimal,
            @RequestParam("responseUrl") responseUrl: String,
            @RequestParam("selectBank", required = false) selectBank: Bank?
    ): ThirdPayValue.SelectPayResult {

        val language = getHeaderLanguage()
        val current = current()
        val orderId = orderIdBuilder.generatorPayOrderId(clientId = current.clientId)

        // 第三方支付
        val bind = payBindService.get(clientId = current.clientId, id = payId)

        val req = PayRequest(orderId = orderId, amount = amount, clientId = current.clientId, memberId = current.id, username = this.currentUsername(),
                payConfig = bind.getConfig(objectMapper), responseUrl = responseUrl, failResponseUrl = responseUrl, language = language,
                payType = bind.payType, selectBank = selectBank)
        val map = payGateway.start(req)

        // 生成订单
        val payCo = PayOrderValue.PayOrderCo(clientId = current.clientId, memberId = current.id, username = this.currentUsername(), orderId = orderId,
                amount = amount, payType = bind.payType, payId = payId, bank = selectBank)
        payOrderService.create(payCo)

        return ThirdPayValue.SelectPayResult(data = map)
    }

//    @GetMapping("/thirdpay/order")
//    override fun pays(
//            @RequestParam(value = "orderId", required = false) orderId: String?,
//            @RequestParam(value = "state", required = false) state: PayState?,
//            @RequestParam(value = "current", defaultValue = "0") current: Int,
//            @RequestParam(value = "size", defaultValue = "10") size: Int
//    ): Page<ThirdPayValue.OrderVo> {
//
//        val member = current()
//
//        val query = PayOrderValue.PayOrderQuery(clientId = member.clientId, memberId = member.id, state = state, orderId = orderId,
//                username = null, current = current, size = size, payType = null, startDate = null, endDate = null)
//        val page = payOrderService.page(query = query)
//        if (page.total == 0) return Page.empty()
//
//        val list = page.data.map {
//            ThirdPayValue.OrderVo(orderId = it.orderId, payType = it.payType, state = it.state, createdTime = it.createdTime,
//                    amount = it.amount)
//        }
//        return Page.of(total = page.total, data = list)
//    }

    @GetMapping("/topup")
    override fun pays(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: PayState?
    ): List<ThirdPayValue.OrderVo> {

        val member = current()

        val query = PayOrderValue.PayOrderQuery(clientId = member.clientId, memberId = member.id, state = state, orderId = orderId,
                username = null, current = 0, size = 200, payType = null, startDate = startDate, endDate = endDate.plusDays(1), memberIds = null)
        val page = payOrderService.page(query = query)
        val list1 = page.data.map {
            ThirdPayValue.OrderVo(orderId = it.orderId, payType = it.payType.name, state = it.state.name, createdTime = it.createdTime,
                    amount = it.amount)
        }

        val depositState = when (state) {
            PayState.Successful -> DepositState.Successful
            PayState.Process -> DepositState.Process
            PayState.Failed -> DepositState.Fail
            PayState.Close -> DepositState.Close
            else -> null
        }
        val deposits = this.deposit(orderId = orderId, state = depositState, current = 0, size = 200, startDate = startDate, endDate = endDate)
        val list2 = deposits.data.map {
            ThirdPayValue.OrderVo(orderId = it.orderId, payType = "Transfer", state = it.state.name, createdTime = it.createdTime,
                    amount = it.money)
        }

        return list1.plus(list2).sortedByDescending { it.createdTime }
    }

    @GetMapping("/daily/report")
    override fun report(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate
    ): List<MemberDailyReportValue.ReportVo> {

        val user = this.current()
        val memberQuery = MemberReportQuery(clientId = user.clientId, memberId = user.id, startDate = startDate, endDate = endDate, agentId = null,
                current = 0, size = 1000, minPromotionAmount = null, minRebateAmount = null)
        val list = memberDailyReportService.query(memberQuery)
        if (list.isEmpty()) return emptyList()

        return list.map { report ->
            with(report) {
                MemberDailyReportValue.ReportVo(memberId = memberId, day = day, settles = settles, totalMWin = totalMWin, totalBet = totalBet,
                        depositAmount = depositAmount.plus(thirdPayAmount), withdrawAmount = withdrawAmount, promotionAmount = promotionAmount,
                        rebateAmount = rebateAmount, rebateExecution = rebateExecution)
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
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: DepositState?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<DepositValue.DepositVo> {

        val (clientId, memberId) = this.currentClientIdAndMemberId()

        val depositQuery = DepositQuery(clientId = clientId, startTime = startDate.atStartOfDay(), endTime = endDate.plusDays(1).atStartOfDay(), orderId = orderId,
                memberId = memberId, state = state, lockWaiterId = null, clientBankIdList = null)

        val page = depositService.query(depositQuery, current, size)
        if (page.total == 0) return Page.empty()

        val data = page.data.map {
            with(it) {
                DepositValue.DepositVo(id = it.id, orderId = it.orderId, money = money, state = it.state, remark = remarks, createdTime = createdTime,
                        endTime = endTime, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberName = memberName,
                        imgPath = imgPath, memberId = memberId, bankOrderId = null, clientBankCardNumber = clientBankCardNumber,
                        clientBankName = clientBankName, clientBankId = clientBankId, lockWaiterId = it.lockWaiterId, depositTime = it.depositTime,
                        channel = it.channel, username = username, clientBank = it.clientBank, lockWaiterUsername = null)
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
                channel = depositCoReq.channel, memberBankId = memberBankId, username = currentUsername(), clientBank = clientBank.bank)
        depositService.create(depositCo)

        return CashDepositResp(orderId = orderId)
    }

    @GetMapping("/withdraw")
    override fun withdraw(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate,
            @RequestParam(value = "orderId", required = false) orderId: String?,
            @RequestParam(value = "state", required = false) state: WithdrawState?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WithdrawValue.WithdrawVo> {

        val (clientId, memberId) = this.currentClientIdAndMemberId()

        val withdrawQuery = WithdrawQuery(clientId = clientId, startTime = startDate.atStartOfDay(), endTime = endDate.plusDays(1).atStartOfDay(), orderId = orderId,
                memberId = memberId, state = state, lockWaiterId = null)

        val page = withdrawService.query(withdrawQuery, current, size)
        if (page.total == 0) return Page.empty()

        val data = page.data.map {

            // 如果是用户 不用显示风险等级
            with(it) {
                WithdrawValue.WithdrawVo(id = it.id, orderId = it.orderId, money = money, state = it.state, remark = remarks, createdTime = createdTime,
                        endTime = endTime, memberBank = memberBank, memberBankCardNumber = memberBankCardNumber, memberName = memberName,
                        memberId = it.memberId, memberBankId = it.memberBankId, lockWaiterId = it.lockWaiterId, username = username,
                        lockWaiterUsername = null, riskLevel = RiskLevel.None)
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

        val watch = StopWatch()
        watch.start()

        val current = this.current()
        val clientId = current.clientId
        val memberId = current.id

        // 检查打码量
        check(withdrawCoReq.money.toDouble() >= 50) { OnePieceExceptionCode.SYSTEM } // 最低出款金额为50
        check(this.checkBet().overBet.toDouble() <= 0) { "打码量不足" }

        // 校验用户是否有充值
        val config = clientConfigService.get(clientId = current.clientId)
        val wallet = walletService.getMemberWallet(memberId = current.id)
        check(wallet.totalDepositBalance.toDouble() >= config.minWithdrawRequire.toDouble()) { OnePieceExceptionCode.NEVER_DEPOSIT }

        // 用户银行卡Id
        val memberBankId = this.bindMemberBank(bankId = withdrawCoReq.memberBankId, bank = withdrawCoReq.bank, bankCardNumber = withdrawCoReq.bankCardNumber)

        // check bank id
        val memberBank = memberBankService.query(memberId).find { it.id == memberBankId }
        checkNotNull(memberBank) { OnePieceExceptionCode.AUTHORITY_FAIL }

        // check safety password
//        memberService.checkSafetyPassword(id = memberId, safetyPassword = withdrawCoReq.safetyPassword)

        val member = memberService.getMember(memberId)

        // create order
        val orderId = orderIdBuilder.generatorWithdrawOrderId()
        val withdrawCo = WithdrawCo(orderId = orderId, clientId = clientId, memberId = memberId,
                memberBank = memberBank.bank, memberBankCardNumber = memberBank.bankCardNumber, memberBankId = memberBank.id,
                money = withdrawCoReq.money, remarks = null, username = member.username, memberName = member.name, role = member.role)
        withdrawService.create(withdrawCo)

        watch.stop()
        log.info("withdraw userId: ${current.id} 耗时：${watch.totalTimeMillis}")

        return CashWithdrawResp(orderId = orderId)
    }

//    @GetMapping("/withdraw/check")
//    override fun checkWithdrawDetail(): CheckWithdrawDetail {
//
//        val user = this.current()
//
//        val now = LocalDateTime.now()
//        val startDate = when {
//            now.hour < 5 -> LocalDate.now().minusDays(1)
//            else -> LocalDate.now()
//        }
//        val totalBet = betOrderService.getTotalBet(clientId = user.clientId, memberId = user.id, startDate = startDate)
//
//        val withdraw = withdrawService.getTotalWithdraw(clientId = user.clientId, memberId = user.id, startDate = startDate)
//
//        return CheckWithdrawDetail(totalBet = totalBet, withdraw = withdraw)
//
//        TODO("Not yet implemented")
//    }

    @GetMapping("/check/promotion")
    override fun checkPromotion(
            @RequestParam("platform") platform: Platform,
            @RequestParam("amount") amount: BigDecimal,
            @RequestParam("promotionId", required = false) promotionId: Int?,
            @RequestParam("code", required = false) code: String?
    ): CheckPromotinResp {

        val language = getHeaderLanguage()
        val current = this.current()

        val member = memberService.getMember(current.id)
        val promotions = promotionService.find(clientId = current.clientId, platform = platform).filter { it.category != PromotionCategory.Backwater }
                .filter { it.category != PromotionCategory.Other }

        log.info("用户：${current.username}, 优惠列表：$promotions")

        val historyOrders = transferOrderService.queryLastPromotion(clientId = current.clientId, memberId = current.id,
                startTime = LocalDateTime.now().minusDays(30))


        val joinPromotions = promotions
                .filter {
                    log.info("用户：${current.username}, 优惠Id：${it.id}, 过滤结果0：${it.code.toUpperCase() == code?.toUpperCase() || it.category != PromotionCategory.ActivationCode} ")
                    it.code.toUpperCase() == code?.toUpperCase() || (it.category != PromotionCategory.ActivationCode && code == null)
                }
                .filter {
                    log.info("用户：${current.username}, 优惠Id：${it.id}, 过滤结果1：${promotionId == null || it.id == promotionId} ")
                    promotionId == null || it.id == promotionId
                }
                .filter {
                    log.info("用户：${current.username}, 优惠Id：${it.id}, 过滤结果2：${it.rule.minAmount.toDouble() <= amount.toDouble() && amount.toDouble() <= it.rule.maxAmount.toDouble()} ")

                    it.rule.minAmount.toDouble() <= amount.toDouble() && amount.toDouble() <= it.rule.maxAmount.toDouble()
                }
                .filter {
                    log.info("用户：${current.username}, 优惠Id：${it.id}, 过滤结果3：${!member.firstPromotion || it.category != PromotionCategory.First} ")
                    !member.firstPromotion || it.category != PromotionCategory.First
                }
                .filter { promotion ->
                    log.info("用户：${current.username}, 优惠Id：${promotion.id}, 过滤结果4：${PromotionPeriod.check(promotion = promotion, historyOrders = historyOrders)} ")

                    PromotionPeriod.check(promotion = promotion, historyOrders = historyOrders)
                }
                .filter { promotion ->
                    log.info("优惠层级：${promotion.levelId}, 用户层级Id：${member.levelId}")
                    log.info("用户：${current.username}, 优惠Id：${promotion.id}, 过滤结果5：${promotion.levelId.isEmpty() || promotion.levelId.contains(member.levelId)} ")
                    promotion.levelId.isEmpty() || promotion.levelId.contains(member.levelId)
//                    promotion.levelId == null || promotion.levelId == member.levelId
                }

        log.info("用户：${current.username}, 可参加优惠列表：$joinPromotions")


        val contentMap = i18nContentService.getConfigType(clientId = current.clientId, configType = I18nConfig.Promotion)
                .map {
                    "${it.configId}:${it.language}" to it
                }.toMap()

        val checkPromotions = joinPromotions.parallelStream().map { promotion ->

            val platformMemberVo = getPromotionPlatformMember(platform, current)
            val (platformBalance, outstanding) = gameApi.getBalanceIncludeOutstanding(clientId = member.clientId, memberId = platformMemberVo.memberId, platform = platform, platformUsername = platformMemberVo.platformUsername,
                    platformPassword = platformMemberVo.platformPassword)

            val platformMember = platformMemberService.get(platformMemberVo.id)

            try {
                val overPromotionAmount = PromotionPeriod.getOverPromotionAmount(promotion = promotion, historyOrders = historyOrders)
                transferUtil.handlerPromotion(platformMember = platformMember, amount = amount, platformBalance = platformBalance, promotionId = promotion.id,
                        overPromotionAmount = overPromotionAmount, outstanding = outstanding)

                val content = contentMap["${promotion.id}:${language}"]
                        ?: contentMap["${promotion.id}:${Language.EN}"]

                content?.let {
                    val mContent = content.getII18nContent(objectMapper = objectMapper) as I18nContent.PromotionI18n

                    val promotionIntroduction = promotion.getPromotionIntroduction(amount = amount, language = language, platformBalance = platformBalance,
                            overPromotionAmount = overPromotionAmount)
                    CheckPromotionVo(promotionId = promotion.id, promotionIntroduction = promotionIntroduction, title = mContent.title)
                }
            } catch (e: Exception) {
                log.error("处理优惠信息错误:", e)
                null
            }
        }.toList().filterNotNull()

        return CheckPromotinResp(promotions = checkPromotions)
    }

    @Synchronized
    private fun getPromotionPlatformMember(platform: Platform, member: JwtUser): PlatformMemberVo {
        return getPlatformMember(platform, member)
    }


    @PutMapping("/transfer")
    @Transactional(rollbackFor = [Exception::class])
    override fun transfer(
            @RequestBody cashTransferReq: CashValue.CashTransferReq
    ): List<BalanceVo> {
        val watch = StopWatch()
        watch.start()

        val current = this.current()

        log.info("用户：${current().username}，开始转账, 优惠Id=${cashTransferReq.promotionId}, 优惠code=${cashTransferReq.code}")
        check(cashTransferReq.amount.toDouble() >= 1 || cashTransferReq.amount.toInt() == -1) { OnePieceExceptionCode.SYSTEM }
//        check(cashTransferReq.to == Platform.Center || cashTransferReq.amount.toInt() == -1) { OnePieceExceptionCode.SYSTEM }

        val promotionId = when {
            cashTransferReq.promotionId != null -> cashTransferReq.promotionId
            !cashTransferReq.code.isNullOrBlank() -> promotionService.all(clientId = current.clientId).firstOrNull{ it.code == cashTransferReq.code}?.id
            else -> null
        }
        // 如果转入的平台是918kiss、pussy、mega 则默认添加优惠为-100
        if (cashTransferReq.to == Platform.Kiss918 || cashTransferReq.to == Platform.Pussy888 || cashTransferReq.to == Platform.Mega) {
            if (promotionId == null) {
                cashTransferReq.promotionId = -100
            }
        }
        log.info("用户：${current().username}，开始转账, 优惠Id=${promotionId}, 优惠code=${cashTransferReq.code}")


        check(cashTransferReq.from != cashTransferReq.to) { OnePieceExceptionCode.AUTHORITY_FAIL }
        check(cashTransferReq.amount.toDouble() > 0 || cashTransferReq.amount.toInt() == -1) { OnePieceExceptionCode.ILLEGAL_OPERATION }

//        if (cashTransferReq.promotionId != null && cashTransferReq.promotionId!! > 0)  {
//            val promotion = promotionService.get(id = cashTransferReq.promotionId!!)
//            val member = memberService.getMember(current.id)
//
//            if   (promotion.category == PromotionCategory.First)  {
//                check(!member.firstPromotion) { OnePieceExceptionCode.ILLEGAL_OPERATION }
//            }
//        }

        // 如果是首存 则提示金额
        val memberIntroduce = promotionId?.let { promotionId ->
            val promotion = promotionService.get(id = promotionId)
            if (promotion.category == PromotionCategory.Introduce) {
                val memberIntroduce = memberIntroduceService.get(memberId = current.id) ?: error(OnePieceExceptionCode.ILLEGAL_OPERATION)
                check(!memberIntroduce.registerActivity) { OnePieceExceptionCode.ILLEGAL_OPERATION }

                val clientConfig = clientConfigService.get(current.clientId)
                check(clientConfig.registerCommission.setScale(2, 2) == cashTransferReq.amount.setScale(2, 2)) {
                    OnePieceExceptionCode.ILLEGAL_OPERATION
                }
                memberIntroduce
            } else null
        }

        if (cashTransferReq.from != Platform.Center) {
            val platformMemberVo = getPlatformMember(platform = cashTransferReq.from, member = current)
            val toCenterTransferReq = cashTransferReq.copy(to = Platform.Center)
            val result = transferUtil.transfer(clientId = current.clientId, platformMemberVo = platformMemberVo, cashTransferReq = toCenterTransferReq, username = currentUsername())
            check(result.transfer) {
                if (result.msg.isBlank()) OnePieceExceptionCode.TRANSFER_FAILED else result.msg
            }
        }

        if (cashTransferReq.to != Platform.Center) {
            val toPlatformTransferReq = cashTransferReq.copy(from = Platform.Center)
            val platformMemberVo = getPlatformMember(platform = cashTransferReq.to, member = current)
            val result = transferUtil.transfer(clientId = current.clientId, platformMemberVo = platformMemberVo, cashTransferReq = toPlatformTransferReq, username = currentUsername())
            check(result.transfer) {
                if (result.msg.isBlank()) OnePieceExceptionCode.TRANSFER_FAILED else result.msg
            }

            memberIntroduce?.let {
                val uo = MemberIntroduceValue.MemberIntroduceUo(id = it.id, registerActivity = true, depositActivity = null, introduceCommission = null)
                memberIntroduceService.update(uo)
            }
        }

        watch.stop()
        log.info("transfer 用户Id：${current.id}, ${cashTransferReq.from} => ${cashTransferReq.to} 耗时：${watch.totalTimeMillis}ms")

        val wallet = walletService.getMemberWallet(current.id)
        return when {
            cashTransferReq.from == Platform.Center -> {
                val fromBalance = BalanceVo(centerBalance = wallet.balance, platform = Platform.Center, balance = wallet.balance, transfer = true, tips = null, totalBet = BigDecimal.ZERO)
                val toBalance = this.balance(platform = cashTransferReq.to)

                listOf(fromBalance, toBalance)
            }
            cashTransferReq.to == Platform.Center -> {
                val fromBalance = this.balance(platform = cashTransferReq.from)
                val toBalance = BalanceVo(centerBalance = wallet.balance, platform = Platform.Center, balance = wallet.balance, transfer = true, tips = null, totalBet = BigDecimal.ZERO)

                listOf(fromBalance, toBalance)
            }
            else -> {
                val fromBalance = this.balance(platform = cashTransferReq.from)
                val toBalance = this.balance(platform = cashTransferReq.to)
                val centerBalance = BalanceVo(centerBalance = wallet.balance, platform = Platform.Center, balance = wallet.balance, transfer = true, tips = null, totalBet = BigDecimal.ZERO)

                listOf(fromBalance, toBalance, centerBalance)
            }
        }

    }

    @PutMapping("/transfer/in/all")
    override fun transferToCenter(): List<CashValue.BalanceAllInVo> {
        val watch = StopWatch()
        watch.start()


        val current = this.current()
        val data = transferUtil.transferInAll(clientId = current.clientId, memberId = current.id, exceptPlatform = null, username = currentUsername())

        watch.stop()
        log.info("transfer 用户Id：${current.id} 耗时：${watch.totalTimeMillis}ms")
        return data
    }


    @GetMapping("/wallet/note")
    override fun walletNote(
            @RequestParam(value = "onlyPromotion", defaultValue = "false") onlyPromotion: Boolean,
            @RequestParam(value = "events", required = false) events: String?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate", required = false) startDate: LocalDate?,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate", required = false) endDate: LocalDate?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): List<WalletNoteVo> {
        val member = this.current()

        val eventList = when (onlyPromotion) {
            true -> listOf(WalletEvent.TRANSFER_OUT)
            false -> events?.let { it.split(",").map { WalletEvent.valueOf(it) } }
        }

        val walletNoteQuery = WalletNoteQuery(clientId = member.clientId, memberId = member.id, current = current, size = size, event = null,
                events = eventList, onlyPromotion = onlyPromotion, startDate = startDate, endDate = endDate?.plusDays(1))
        val list = walletNoteService.query(walletNoteQuery)
        return list.map {
            WalletNoteVo(id = it.id, memberId = it.memberId, eventId = it.eventId, event = it.event, money = it.money, remarks = it.remarks, createdTime = it.createdTime,
                    promotionMoney = it.promotionMoney)
        }
    }

    @GetMapping("/wallet/note/page")
    override fun walletNotePage(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "startDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "endDate") endDate: LocalDate,
            @RequestParam(value = "onlyPromotion", defaultValue = "false") onlyPromotion: Boolean,
            @RequestParam(value = "events", required = false) events: String?,
            @RequestParam("current") current: Int,
            @RequestParam("size") size: Int
    ): Page<WalletNoteVo> {
        val member = this.current()

        val eventList = when (onlyPromotion) {
            true -> listOf(WalletEvent.TRANSFER_OUT)
            false -> events?.let { it.split(",").map { WalletEvent.valueOf(it) } }
        }

        val walletNoteQuery = WalletNoteQuery(clientId = member.clientId, memberId = member.id, current = current, size = size, event = null,
                events = eventList, onlyPromotion = onlyPromotion, startDate = startDate, endDate = endDate.plusDays(1))

        val total = walletNoteService.total(walletNoteQuery)
        if (total <= 0) return Page.empty()

        val notes = walletNoteService.query(walletNoteQuery)
        val list = notes.map {
            WalletNoteVo(id = it.id, memberId = it.memberId, eventId = it.eventId, event = it.event, money = it.money, remarks = it.remarks, createdTime = it.createdTime,
                    promotionMoney = it.promotionMoney)
        }

        return Page.of(total = total, data = list)
    }

    @GetMapping("/balance")
    override fun balance(
            @RequestHeader("platform") platform: Platform
    ): BalanceVo {
        val member = current()

        val language = getHeaderLanguage()

        val walletBalance = walletService.getMemberWallet(current().id).balance

        return when (platform) {
            Platform.Center -> {
                BalanceVo(platform = platform, balance = walletBalance, transfer = true, tips = null, centerBalance = walletBalance, totalBet = BigDecimal.ZERO)
            }
            else -> {
                // 判断用户是否有参加活动
                val platformMemberVo = getPlatformMember(platform, member)
                val platformMember = platformMemberService.get(platformMemberVo.id)

                val platformBalance = gameApi.balance(clientId = member.clientId, memberId = platformMemberVo.memberId, platformUsername = platformMemberVo.platformUsername, platform = platform,
                        platformPassword = platformMember.password)
                val (transfer, tips) = this.checkCanTransferOutAndTips(platformMember = platformMember, platformBalance = platformBalance, language = language)


                val totalBet = when (platform) {
                    Platform.Kiss918, Platform.Pussy888, Platform.Mega -> BigDecimal.valueOf(-1)
                    else -> platformMember.totalBet
                }
                BalanceVo(platform = platform, balance = platformBalance, transfer = transfer, tips = tips, centerBalance = walletBalance, totalBet = totalBet)
            }
        }
    }

    @GetMapping("/balances")
    override fun balances(
            @RequestParam("category", required = false) category: PlatformCategory?
    ): List<BalanceVo> {

        val language = getHeaderLanguage()
        val member = this.current()
        val clientId = member.clientId
        val memberId = member.id

        // 查询主钱包
        val wallet = walletService.getMemberWallet(memberId = memberId)
        val walletBalanceVo = BalanceVo(platform = Platform.Center, balance = wallet.balance, transfer = true, tips = null, centerBalance = wallet.balance, totalBet = BigDecimal.valueOf(-1))

        // 查询厅主开通的平台列表
        val platforms = platformBindService.findClientPlatforms(clientId)
                .filter { it.status != Status.Delete }

        // 查询用户开通的平台列表
        val platformMemberMap = platformMemberService.findPlatformMember(memberId = memberId).map { it.platform to it }.toMap()

        // 查询周打码量
        val today = LocalDate.now()
        val startDate = today.with(DayOfWeek.MONDAY)
        val endDate = today.with(DayOfWeek.SUNDAY).plusDays(1)
        val query = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate, minRebateAmount = null, minPromotionAmount = null, current = 0,
                size = 999999, agentId = null)
        val reports = memberDailyReportService.query(query)
        val todayReport = betOrderService.report(memberId = memberId, startDate = today, endDate = today.plusDays(1))
                .map { it.platform to it.totalBet }.toMap()
        val reportMap = if (reports.isNotEmpty()) {
            reports.map { it.settles }.reduce { acc, list -> acc.plus(list) }.groupBy { it.platform }
                    .map { it.key to (it.value.sumByDouble { a -> a.bet.toDouble() }.toBigDecimal().setScale(2, 2)) }.toMap()
        } else {
            emptyMap()
        }


        // 查询余额 //TODO 暂时用简单的异步去处理
        val balances = platforms.filter { category == null || it.platform.category == category }.parallelStream().map {

            val watch = System.currentTimeMillis()
            val platformMember = platformMemberMap[it.platform]

            when (platformMember == null) {
                true -> BalanceVo(platform = it.platform, balance = BigDecimal.ZERO, transfer = true, tips = null, centerBalance = wallet.balance, totalBet = BigDecimal.ZERO)
                else -> {
                    val platformBalance = try {
                        gameApi.balance(clientId = clientId, memberId = platformMember.memberId, platformUsername = platformMember.username, platform = it.platform,
                                platformPassword = platformMember.password)
                    } catch (e: Exception) {
                        BigDecimal.valueOf(-1)
                    }

                    // 查询总下注金额
                    val totalBet = when (it.platform) {
                        Platform.Kiss918, Platform.Pussy888, Platform.Mega -> BigDecimal.valueOf(-1)
                        else -> platformMember.totalBet
                    }

                    // 查询本周下注金额
                    val historyBet = reportMap[it.platform] ?: BigDecimal.ZERO
                    val todayBet = todayReport[it.platform] ?: BigDecimal.ZERO


                    val (transfer, tips) = this.checkCanTransferOutAndTips(platformMember = platformMember, platformBalance = platformBalance, language = language)
                    BalanceVo(platform = it.platform, balance = platformBalance, transfer = transfer, tips = tips, centerBalance = wallet.balance, totalBet = totalBet,
                            weekBet = historyBet.plus(todayBet))
                }
            }.let {
                log.info("平台：${it.platform}, 查询余额耗时：${System.currentTimeMillis() - watch}ms")
                it
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
                val tips = when (language) {
                    Language.CN -> {
                        "转出需要打码量:${platformMember.requirementBet}, 当前打码量:${platformMember.currentBet}"
                    }
                    else -> {
                        "Turnover Requirement:${platformMember.requirementBet}, Current Total Bet:${platformMember.currentBet}\n"
                    }
                }
                transfer to tips
            }
            promotion.ruleType == PromotionRuleType.Withdraw -> {
                val transfer = platformMember.requirementTransferOutAmount.toDouble() <= platformBalance.toDouble()
                val tips = when (language) {
                    Language.CN -> {
                        "转出需要最小金额:${platformMember.requirementTransferOutAmount.toDouble()}, 当前平台金额:${platformBalance.toDouble()}"
                    }
                    else -> {
                        "Minimum Balance Requirement:${platformMember.requirementTransferOutAmount.toDouble()}, Current Balance:${platformBalance.toDouble()}\n"
                    }
                }
                transfer to tips
            }
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }
    }

}












