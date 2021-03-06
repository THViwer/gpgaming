package com.onepiece.gpgaming.web.controller

import com.alibaba.excel.EasyExcel
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.MemberBank
import com.onepiece.gpgaming.beans.model.Vip
import com.onepiece.gpgaming.beans.value.database.DepositQuery
import com.onepiece.gpgaming.beans.value.database.LevelValue
import com.onepiece.gpgaming.beans.value.database.MemberBankUo
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.beans.value.database.PayOrderValue
import com.onepiece.gpgaming.beans.value.database.VipValue
import com.onepiece.gpgaming.beans.value.database.WalletQuery
import com.onepiece.gpgaming.beans.value.database.WithdrawQuery
import com.onepiece.gpgaming.beans.value.internet.web.LevelCoReq
import com.onepiece.gpgaming.beans.value.internet.web.LevelMemberVo
import com.onepiece.gpgaming.beans.value.internet.web.LevelMoveDo
import com.onepiece.gpgaming.beans.value.internet.web.LevelUoReq
import com.onepiece.gpgaming.beans.value.internet.web.LevelVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberBankValue
import com.onepiece.gpgaming.beans.value.internet.web.MemberCoReq
import com.onepiece.gpgaming.beans.value.internet.web.MemberPage
import com.onepiece.gpgaming.beans.value.internet.web.MemberUoReq
import com.onepiece.gpgaming.beans.value.internet.web.MemberValue
import com.onepiece.gpgaming.beans.value.internet.web.MemberVo
import com.onepiece.gpgaming.beans.value.internet.web.MemberWalletInfo
import com.onepiece.gpgaming.beans.value.internet.web.UserValue
import com.onepiece.gpgaming.beans.value.internet.web.WalletVo
import com.onepiece.gpgaming.core.dao.DepositDao
import com.onepiece.gpgaming.core.dao.PayOrderDao
import com.onepiece.gpgaming.core.dao.WithdrawDao
import com.onepiece.gpgaming.core.service.ClientConfigService
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.DepositService
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.core.service.MemberBankService
import com.onepiece.gpgaming.core.service.MemberDailyReportService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.PayOrderService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.core.service.ReportService
import com.onepiece.gpgaming.core.service.VipService
import com.onepiece.gpgaming.core.service.WaiterService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.core.service.WithdrawService
import com.onepiece.gpgaming.games.GameApi
import com.onepiece.gpgaming.games.http.OkHttpUtil
import com.onepiece.gpgaming.utils.StringUtil
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.math.BigDecimal
import java.time.LocalDate
import java.util.stream.Collectors


@RestController
class MemberApiController(
        private val memberService: MemberService,
        private val walletService: WalletService,
        private val levelService: LevelService,
        private val depositService: DepositService,
        private val withdrawService: WithdrawService,
        private val platformMemberService: PlatformMemberService,
        private val gameApi: GameApi,
        private val memberBankService: MemberBankService,
        private val payOrderService: PayOrderService,
        private val clientService: ClientService,
        private val waiterService: WaiterService,

        private val depositDao: DepositDao,
        private val withdrawDao: WithdrawDao,
        private val payOrderDao: PayOrderDao,

        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val okHttpUtil: OkHttpUtil,

        private val vipService: VipService,
        private val reportService: ReportService,
        private val memberDailyReportService: MemberDailyReportService,

        private val clientConfigService: ClientConfigService

) : BasicController(), MemberApi {

    companion object {
        private const val HASH_CODE = "28b419c9-08aa-40d1-9bc1-ea59ddf751f0"
    }


    @GetMapping("/member")
    override fun query(
            @RequestParam(value = "username", required = false) username: String?,
            @RequestParam(value = "name", required = false) name: String?,
            @RequestParam(value = "phone", required = false) phone: String?,
            @RequestParam(value = "levelId", required = false) levelId: Int?,
            @RequestParam(value = "status", required = false) status: Status?,
            @RequestParam(value = "promoteCode", required = false) promoteCode: String?,
            @RequestParam(value = "current", defaultValue = "0") current: Int,
            @RequestParam(value = "size", defaultValue = "10") size: Int
    ): MemberPage {


        val clientId = if (username != null) {
            val bossId = getBossId()
            val member = memberService.findByBossIdAndUsername(bossId = bossId, username = username)
                    ?: return MemberPage(data = emptyList(), total = 0)
            member.clientId
        } else {
            getClientId()
        }
        val client = clientService.get(clientId)


        val query = MemberQuery(clientId = clientId, startTime = null, endTime = null, username = username,
                levelId = levelId, status = status, promoteCode = promoteCode, name = name, phone = phone,
                role = Role.Member, bossId = null, agentId = null)
        val page = memberService.query(query, current, size)
        if (page.total == 0) return MemberPage(total = 0, data = emptyList())

        val levels = levelService.all(clientId).map { it.id to it }.toMap()

        val ids = page.data.map { it.id }
        val walletQuery = WalletQuery(clientId = clientId, memberIds = ids)
        val memberMap = walletService.query(walletQuery).map { it.memberId to it }.toMap()

        val agentIds = page.data.map { it.agentId }
        val agentQuery = MemberQuery(ids = agentIds)
        val agentMap = memberService.query(memberQuery = agentQuery, current = 0, size = 999999)
                .data
                .map { it.id to it }
                .toMap()

        val sales = waiterService.findClientWaiters(clientId = clientId).filter { it.role == Role.Sale }
        val saleMap = sales.map { it.id to it }.toMap()

        val data = page.data.map {

            val agent = agentMap[it.agentId]
            val (agentId, agentUsername) = (agent?.id ?: -1) to (agent?.username ?: "-")

            val sale = saleMap[it.saleId]
            val saleUsername = sale?.username ?: "-"

            with(it) {
                MemberVo(id = id, username = it.username, levelId = it.levelId, level = levels[it.levelId]?.name ?: "error level",
                        balance = memberMap[it.id]?.balance ?: BigDecimal.valueOf(-1), status = it.status, createdTime = createdTime,
                        loginIp = loginIp, loginTime = loginTime, name = it.name, phone = it.phone, promoteCode = it.promoteCode, idCard = it.idCard,
                        country = client.country, agentId = agentId, agentUsername = agentUsername, saleId = it.saleId, saleUsername = saleUsername,
                        registerIp = it.registerIp, riskLevel = it.riskLevel, address = it.address, email = it.email, birthday = it.birthday,
                        marketId = it.marketId, saleScope = it.saleScope)
            }
        }

        return MemberPage(total = page.total, data = data)
    }

    @GetMapping("/member/excel")
    override fun excelMembers() {

        val user = this.current()
        check(user.role == Role.Client)

        val today = LocalDate.now().toString().replace("-", "")

        val response = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).response!!
        val name = "members_$today"

        response.contentType = "application/vnd.ms-excel"
        response.characterEncoding = "utf-8"
        response.setHeader("Content-disposition", "attachment;filename=$name.xlsx")

        val memberQuery = MemberQuery(clientId = user.id, role = Role.Member)
        val data = memberService.list(memberQuery)
                .map {
                    MemberValue.MemberExcelVo(username = it.username, name = it.name, phone = it.phone, email = it.email)
                }

        EasyExcel.write(response.outputStream, MemberValue.MemberExcelVo::class.java)
                .autoCloseStream(false)
                .sheet("member")
                .doWrite(data)
    }

    @GetMapping("/risk/detail/{memberId}")
    override fun checkRiskDetail(@PathVariable("memberId") memberId: Int): MemberValue.RiskDetail {

        val user = this.current()
        val member = memberService.getMember(id = memberId)

        val memberQuery = MemberQuery(bossId = user.bossId, clientId = user.clientId, name = member.name)
        val theSameNames = memberService.list(memberQuery).filter { it.id != memberId }

        val registerIp = if (member.registerIp == "admin:register" || member.registerIp == "agent:register" || member.registerIp == "None") null else member.registerIp
        val theRegisterIps = registerIp?.let {
            memberService.list(memberQuery.copy(name = null, registerIp = member.registerIp)).filter { it.id != memberId }
        } ?: emptyList()

        val sameNameList = theSameNames.map {
            MemberValue.RiskDetail.RiskMemberVo(memberId = it.id, username = it.username, name = it.name, loginIp = it.loginIp ?: "-", registerIp = it.registerIp)
        }

        val registerIpList = theRegisterIps.map {
            MemberValue.RiskDetail.RiskMemberVo(memberId = it.id, username = it.username, name = it.name, loginIp = it.loginIp ?: "-", registerIp = it.registerIp)
        }

        return MemberValue.RiskDetail(sameNameList = sameNameList, sameRegisterIpList = registerIpList)
    }

    @GetMapping("/member/login")
    override fun loginByAdmin(@RequestParam("username") username: String): UserValue.MemberLoginResponse {

        val bossId = getBossId()
        val clientId = getClientId()
        val time = System.currentTimeMillis()

        val pwdStr = "${time}:$HASH_CODE:${username}"
        val hash = bCryptPasswordEncoder.encode(pwdStr)


        val webSite = webSiteService.getDataByBossId(bossId = bossId).first { it.clientId == clientId }
        val req = UserValue.MemberLoginReq(clientId = clientId, username = username, time = time, hash = hash)

        val url = "https://www.${webSite.domain}/api/v1/player/user/login_from_admin"
//        val url = "http://localhost:8002/api/v1/player/user/login_from_admin"
        return okHttpUtil.doPostJson(platform = Platform.CT, url = url, data = req, clz = UserValue.MemberLoginResponse::class.java)
    }

    @GetMapping("/member/follow")
    override fun follow(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerStartDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerEndDate") endDate: LocalDate
    ): List<MemberValue.FollowVo> {

        val clientId = getClientId()
        val startTime = startDate.atStartOfDay()
        val endTime = endDate.atStartOfDay()

        // ????????????
        val query = MemberQuery(clientId = clientId, startTime = startTime, endTime = endTime, status = Status.Normal,
                levelId = null, name = null, username = null, phone = null, promoteCode = null, role = null, agentId = null,
                bossId = null)
        val members = memberService.query(query, current = 0, size = 999999).data
        if (members.isEmpty()) return emptyList()
        val memberIds = members.map { it.id }.toList()

        // ??????
        val depositQuery = DepositQuery(clientId = clientId, startTime = startTime, endTime = endTime, memberIds = memberIds)
        val deposits = depositDao.query(depositQuery, 0, 9999)
                .groupBy { it.memberId }

        val payQuery = PayOrderValue.PayOrderQuery(clientId = clientId, memberId = null, startDate = startDate, endDate = endDate, current = 0,
                size = 99999, orderId = null, payType = null, state = null, username = null, memberIds = memberIds)
        val pays = payOrderDao.query(payQuery)
                .groupBy { it.memberId }

        // ??????
        val withdrawQuery = WithdrawQuery(clientId = clientId, memberId = null, startTime = startTime, endTime = endTime, memberIds = memberIds)
        val withdraws = withdrawDao.query(query = withdrawQuery, current = 0, size = 999999)
                .groupBy { it.memberId }


        return members.map { member ->

            val mDeposits = deposits[member.id] ?: emptyList()
            val mPays = pays[member.id] ?: emptyList()

            val depositCount = mDeposits.count().plus(mPays.count())
            val depositMoney = mDeposits.sumByDouble { it.money.toDouble() }.plus(mPays.sumByDouble { it.amount.toDouble() })
                    .toBigDecimal().setScale(2, 2)
            val lastDepositTime = mDeposits.maxBy { it.createdTime }.let { a ->

                val b = mPays.maxBy { it.createdTime }
                when {
                    a == null -> b?.createdTime
                    b == null -> a.createdTime
                    else -> if (a.createdTime > b.createdTime) a.createdTime else b.createdTime
                }
            }


            val mWithdraws = withdraws[member.id] ?: emptyList()
            val withdrawCount = mWithdraws.count()
            val withdrawMoney = mWithdraws.sumByDouble { it.money.toDouble() }
                    .toBigDecimal().setScale(2, 2)
            val lastWithdrawTime = mWithdraws.maxBy { it.createdTime }?.createdTime


            MemberValue.FollowVo(memberId = member.id, username = member.username, phone = member.phone, registerTime = member.createdTime,
                    lastLoginTime = member.loginTime, depositMoney = depositMoney, depositCount = depositCount, withdrawMoney = withdrawMoney,
                    withdrawCount = withdrawCount, lastDepositTime = lastDepositTime, lastWithdrawTime = lastWithdrawTime)
        }
    }

    @GetMapping("/member/follow/excel")
    override fun followExcel(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerStartDate") startDate: LocalDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(value = "registerEndDate") endDate: LocalDate
    ) {

        val response = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).response!!
        val name = "follow_${startDate.toString().replace("-", "")}_${endDate.toString().replace("_", "")}"

        response.contentType = "application/vnd.ms-excel"
        response.characterEncoding = "utf-8"
        response.setHeader("Content-disposition", "attachment;filename=$name.xlsx")

        val data = this.follow(startDate = startDate, endDate = endDate)

        EasyExcel.write(response.outputStream, MemberValue.FollowVo::class.java).autoCloseStream(false).sheet("member").doWrite(data)
    }

    @GetMapping("/member/info")
    override fun getWalletInfo(@RequestParam("memberId") memberId: Int): MemberWalletInfo {

        val clientId = getClientId()

        val wallet = walletService.getMemberWallet(memberId = memberId)

        val depositQuery = DepositQuery(clientId = wallet.clientId, memberId = memberId, size = 5)
        val depositHistory = depositService.query(depositQuery)

        val withdrawQuery = WithdrawQuery(clientId = wallet.clientId, memberId = memberId, size = 5)
        val withdrawHistory = withdrawService.query(withdrawQuery)


        val thirdQuery = PayOrderValue.PayOrderQuery(clientId = clientId, memberId = memberId, current = 0, size = 5,
                startDate = null, endDate = null, orderId = null, payType = null, state = null, username = null, memberIds = null)
        val lastPayOrders = payOrderService.query(thirdQuery)

        val platformMembers = platformMemberService.findPlatformMember(memberId)
        val balances = platformMembers.stream().map { platformMember ->

            val balance = try {
                gameApi.balance(clientId = platformMember.clientId, memberId = platformMember.memberId, platform = platformMember.platform, platformUsername = platformMember.username,
                        platformPassword = platformMember.password)
            } catch (e: Exception) {
                BigDecimal.valueOf(-1)
            }

            val lastBet = platformMember.joinPromotionId?.let {
                platformMember.requirementBet.minus(platformMember.currentBet)
            } ?: BigDecimal.ZERO

            MemberWalletInfo.BalanceVo(platform = platformMember.platform, balance = balance, totalWin = platformMember.totalWin, totalBet = platformMember.totalBet,
                    totalAmount = platformMember.totalAmount, totalPromotionAmount = platformMember.totalPromotionAmount, totalTransferOutAmount = platformMember.totalTransferOutAmount,
                    pusername = platformMember.username, ppassword = platformMember.password, lastBet = lastBet, joinPromotionId = platformMember.joinPromotionId)
        }.collect(Collectors.toList()).toList().sortedByDescending { it.balance }

        // ??????????????????
        val today = LocalDate.now()
//        val todayList = reportService.startMemberReport(memberId = memberId, startDate = today)
        val mdrQuery = MemberReportQuery(clientId = clientId, agentId = null, memberId = memberId, startDate = today.minusDays(6), endDate = today, current = 0,
                size = 10, minRebateAmount = null, minPromotionAmount = null)
        val history = memberDailyReportService.query(mdrQuery)
//        val weekReports = todayList.plus(history).sortedByDescending { it.day }

        return MemberWalletInfo(memberId = memberId, wallet = wallet, lastFiveDeposit = depositHistory, lastFiveWithdraw = withdrawHistory,
                balances = balances, lastPayOrders = lastPayOrders)

    }

    @PutMapping("/member")
    override fun update(@RequestBody memberUoReq: MemberUoReq) {
        val clientId = getClientId()

        val member = memberService.getMember(memberUoReq.id)
        check(member.clientId == clientId) { OnePieceExceptionCode.AUTHORITY_FAIL }

        val memberUo = MemberUo(id = memberUoReq.id, levelId = memberUoReq.levelId, password = memberUoReq.password,
                status = memberUoReq.status, name = memberUoReq.name, phone = memberUoReq.phone, saleId = memberUoReq.saleId,
                email = memberUoReq.email, address = memberUoReq.address, idCard = memberUoReq.idCard, birthday = memberUoReq.birthday)
        memberService.update(memberUo)
    }

    @PostMapping("/member")
    override fun create(@RequestBody memberCoReq: MemberCoReq) {
        val clientId = getClientId()
        val bossId = getBossId()

        check(memberCoReq.levelId > 0) { OnePieceExceptionCode.DATA_FAIL }

        //TODO ?????????????????????
        val promoteCode = memberCoReq.promoteCode ?: StringUtil.generateNonce(6)

        // ??????Id
        val agentId = memberCoReq.agentId ?: memberService.getDefaultAgent(bossId = bossId).id

        val saleId = memberCoReq.saleCode?.toInt() ?: -1

        val memberCo = MemberCo(clientId = clientId, username = memberCoReq.username, password = memberCoReq.password, promoteCode = promoteCode,
                safetyPassword = memberCoReq.safetyPassword, levelId = memberCoReq.levelId, name = memberCoReq.name, phone = memberCoReq.phone, bossId = bossId,
                agentId = agentId, role = memberCoReq.role, formal = true, saleId = saleId, registerIp = "admin:register",
                birthday = memberCoReq.birthday, email = memberCoReq.email)
        memberService.create(memberCo)
    }

    @GetMapping("/member/wallet/{memberId}")
    override fun balance(
            @PathVariable(value = "memberId") memberId: Int
    ): WalletVo {

        val wallet = walletService.getMemberWallet(memberId)

        return with(wallet) {
            WalletVo(id = wallet.id, memberId = wallet.memberId, balance = balance, freezeBalance = freezeBalance,
                    totalGiftBalance = totalGiftBalance, totalDepositBalance = totalDepositBalance, totalWithdrawBalance = totalWithdrawBalance)
        }

//        return when (platform) {
//            Platform.Center -> walletVo
//            else -> {
//                TODO ????????????????????????
//                walletVo.copy(balance = BigDecimal.valueOf(100))
//            }

//        }
    }

    @GetMapping("/member/bank/{memberId}")
    override fun banks(@PathVariable(value = "memberId") memberId: Int): List<MemberBank> {
        return memberBankService.query(memberId = memberId)
    }

    @PutMapping("/member/bank")
    override fun bankUo(@RequestBody req: MemberBankValue.MemberBankUo) {

        val banks = memberBankService.query(memberId = req.memberId)
        val existBank = banks.firstOrNull { it.bank == req.bank && it.id != req.id }
        check(existBank == null) { OnePieceExceptionCode.MEMBER_BANK_EXIST }

        val uo = MemberBankUo(id = req.id, bank = req.bank, bankCardNumber = req.bankCardNumber, status = null)
        memberBankService.update(uo)
    }


    @GetMapping("/level")
    override fun all(): List<LevelVo> {
        val clientId = getClientId()

        val levelCountMap = memberService.getLevelCount(clientId)

        return levelService.all(clientId).map {
            val count = levelCountMap[it.id] ?: 0
            LevelVo(id = it.id, name = it.name, status = it.status, createdTime = it.createdTime, total = count, sportRebate = it.sportRebate,
                    liveRebate = it.liveRebate, slotRebate = it.slotRebate, fishRebate = it.fishRebate)
        }
    }

    @GetMapping("/level/normal")
    override fun normalList(): List<LevelVo> {
        return all().filter { it.status == Status.Normal }
    }

    @PostMapping("/level")
    override fun create(@RequestBody levelCoReq: LevelCoReq) {
        val clientId = getClientId()
        val levelCo = LevelValue.LevelCo(clientId = clientId, name = levelCoReq.name, sportRebate = levelCoReq.sportRebate, liveRebate = levelCoReq.liveRebate,
                slotRebate = levelCoReq.slotRebate, fishRebate = levelCoReq.fishRebate)
        levelService.create(levelCo)
    }

    @PutMapping("/level")
    override fun update(@RequestBody levelUoReq: LevelUoReq) {
        val levelUo = LevelValue.LevelUo(id = levelUoReq.id, name = levelUoReq.name, status = levelUoReq.status, sportRebate = levelUoReq.sportRebate,
                liveRebate = levelUoReq.liveRebate, slotRebate = levelUoReq.slotRebate, fishRebate = levelUoReq.fishRebate)
        levelService.update(levelUo)
    }


    @GetMapping("/vip")
    override fun vipList(): List<Vip> {
        val user = this.current()
        return vipService.list(clientId = user.clientId)
    }

    @PostMapping("/vip")
    override fun vipCreate(@RequestBody co: VipValue.VipCo) {
        val user = this.current()
        vipService.create(co.copy(clientId = user.clientId))
    }

    @PutMapping("/vip")
    override fun vipUpdate(@RequestBody uo: VipValue.VipUo) {
//        val user = this.current()
        vipService.update(uo)
    }

    @GetMapping("/level/member")
    override fun findMembers(
            @RequestParam("username", required = false) username: String?,
            @RequestParam("levelId", required = false) levelId: Int?,
            @RequestParam("minBalance", required = false) minBalance: BigDecimal?,
            @RequestParam("maxBalance", required = false) maxBalance: BigDecimal?,
            @RequestParam("minTotalDepositBalance", required = false) minTotalDepositBalance: BigDecimal?,
            @RequestParam("maxTotalDepositBalance", required = false) maxTotalDepositBalance: BigDecimal?,
            @RequestParam("minTotalWithdrawBalance", required = false) minTotalWithdrawBalance: BigDecimal?,
            @RequestParam("maxTotalWithdrawBalance", required = false) maxTotalWithdrawBalance: BigDecimal?,
            @RequestParam("minTotalDepositFrequency", required = false) minTotalDepositFrequency: Int?,
            @RequestParam("maxTotalDepositFrequency", required = false) maxTotalDepositFrequency: Int?,
            @RequestParam("minTotalWithdrawFrequency", required = false) minTotalWithdrawFrequency: Int?,
            @RequestParam("maxTotalWithdrawFrequency", required = false) maxTotalWithdrawFrequency: Int?
    ): List<LevelMemberVo> {
        val clientId = getClientId()

        check(
                username == null
                        || minBalance == null || maxBalance == null
                        || minTotalDepositBalance == null || maxTotalDepositBalance == null
                        || minTotalWithdrawBalance == null || maxTotalWithdrawBalance == null
                        || minTotalDepositFrequency == null || maxTotalDepositFrequency == null
                        || minTotalWithdrawFrequency == null || maxTotalWithdrawFrequency == null) { OnePieceExceptionCode.QUERY_COUNT_TOO_SMALL }

        val memberId = when (username != null) {
            true -> memberService.findByUsername(clientId, username)?.id ?: return emptyList()
            false -> null
        }

        // ??????????????????
        val walletQuery = WalletQuery(clientId = clientId, memberId = memberId, minBalance = minBalance, minTotalDepositBalance = minTotalDepositBalance,
                minTotalDepositFrequency = minTotalDepositFrequency, minTotalWithdrawBalance = minTotalWithdrawBalance, minTotalWithdrawFrequency = minTotalWithdrawFrequency,
                maxBalance = maxBalance, maxTotalDepositBalance = maxTotalDepositBalance, maxTotalDepositFrequency = maxTotalDepositFrequency,
                maxTotalWithdrawBalance = maxTotalWithdrawBalance, maxTotalWithdrawFrequency = maxTotalWithdrawFrequency)
        val wallets = walletService.query(walletQuery)
        if (wallets.isEmpty()) return emptyList()
        val memberIds = wallets.map { it.memberId }
        val walletMap = wallets.map { it.memberId to it }.toMap()

        // ??????????????????
        val members = memberService.findByIds(levelId = levelId, ids = memberIds)

        // ??????????????????
        val levels = levelService.all(clientId).map { it.id to it }.toMap()

        // ????????????
        return members.filter { walletMap[it.id] != null }.map { member ->
            val wallet = walletMap[member.id] ?: error(OnePieceExceptionCode.DATA_FAIL)

            val level = levels[member.levelId] ?: error(OnePieceExceptionCode.DATA_FAIL)

            LevelMemberVo(memberId = member.id, username = member.username, balance = wallet.balance, freezeBalance = wallet.freezeBalance, totalDepositBalance = wallet.totalDepositBalance,
                    totalWithdrawBalance = wallet.totalWithdrawBalance, totalGiftBalance = wallet.totalGiftBalance, totalWithdrawFrequency = wallet.totalWithdrawFrequency,
                    totalDepositFrequency = wallet.totalDepositFrequency, loginTime = member.loginTime, levelId = level.id, levelName = level.name)
        }


    }

    @PutMapping("/level/move")
    override fun move(@RequestBody levelMoveDo: LevelMoveDo) {
        val clientId = getClientId()

        //TODO ????????????????????????????????????
        check(levelMoveDo.memberIds.isNotEmpty()) { OnePieceExceptionCode.MOVE_LEVEL_COUNT_ISZERO }
        check(levelMoveDo.memberIds.size <= 2000) { OnePieceExceptionCode.MOVE_LEVEL_COUNT_ISMAX }

        memberService.moveLevel(clientId = clientId, levelId = levelMoveDo.levelId, memberIds = levelMoveDo.memberIds)
    }

    @GetMapping("/sale/move")
    override fun saleMove(
            @RequestParam("fromSaleId") fromSaleId: Int,
            @RequestParam("toSaleId") toSaleId: Int
    ) {

        val user = this.current()

        val fromWaiter = waiterService.get(id = fromSaleId)
        val toWaiter = waiterService.get(id = toSaleId)

        check(fromWaiter.role == Role.Sale) { "from sale id error" }
        check(toWaiter.role == Role.Sale) { "to sale id error" }

        memberService.moveSale(clientId = user.clientId, fromSaleId = fromSaleId, toSaleId = toSaleId)
    }
}