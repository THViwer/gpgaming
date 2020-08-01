package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.CommissionType
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.SaleScope
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.AgentDailyReport
import com.onepiece.gpgaming.beans.model.AgentMonthReport
import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.model.Level
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.model.MemberPlatformDailyReport
import com.onepiece.gpgaming.beans.model.SaleDailyReport
import com.onepiece.gpgaming.beans.model.SaleMonthReport
import com.onepiece.gpgaming.beans.value.database.AnalysisValue
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberReportValue
import com.onepiece.gpgaming.core.dao.AnalysisDao
import com.onepiece.gpgaming.core.dao.BetOrderDao
import com.onepiece.gpgaming.core.dao.LevelDao
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.dao.MemberDao
import com.onepiece.gpgaming.core.dao.OtherPlatformReportDao
import com.onepiece.gpgaming.core.dao.SaleDailyReportDao
import com.onepiece.gpgaming.core.dao.TransferOrderDao
import com.onepiece.gpgaming.core.dao.TransferReportQuery
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.core.service.CommissionService
import com.onepiece.gpgaming.core.service.ReportService
import com.onepiece.gpgaming.core.service.WaiterService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@Service
class ReportServiceImpl(
        private val transferOrderDao: TransferOrderDao,
        private val memberDao: MemberDao,
        private val betOrderService: BetOrderService,
        private val betOrderDao: BetOrderDao,
        private val levelDao: LevelDao,
        private val analysisDao: AnalysisDao,
        private val commissionService: CommissionService,
        private val otherPlatformReportDao: OtherPlatformReportDao,
        private val memberDailyReportDao: MemberDailyReportDao,
        private val waiterService: WaiterService,
        private val saleDailyReportDao: SaleDailyReportDao
) : ReportService {

    private val log = LoggerFactory.getLogger(ReportServiceImpl::class.java)

    override fun startMemberPlatformDailyReport(startDate: LocalDate): List<MemberPlatformDailyReport> {

        val endDate = startDate.plusDays(1)
        val now = LocalDateTime.now()

        // 转账报表
        val transferQuery = TransferReportQuery(clientId = null, memberId = null, from = null, to = null, startDate = startDate, endDate = endDate)
        val transferReports = transferOrderDao.memberPlatformReport(query = transferQuery)


        return transferReports.groupBy { "${it.memberId}:${it.platform}" }.map { maps ->

            val transferInReport = maps.value.find { it.from == Platform.Center }
            val transferOutReport = maps.value.find { it.to == Platform.Center }

            val first = maps.value.first()
            val platform = if (first.from == Platform.Center) first.to else first.from
            MemberPlatformDailyReport(id = -1, clientId = first.clientId, day = startDate, memberId = first.memberId, platform = platform,
                    transferIn = transferInReport?.money ?: BigDecimal.ZERO, transferOut = transferOutReport?.money ?: BigDecimal.ZERO, createdTime = now, status = Status.Normal)
        }

    }

    override fun startMemberReport(startDate: LocalDate): List<MemberDailyReport> {
        val endDate = startDate.plusDays(1)

        // 报表数据
        val list = analysisDao.memberReport(startDate = startDate, endDate = endDate)

        // 会员对应返水比例
        val levelIds = levelDao.all().map { it.id to it }.toMap()

        // 平台报表 输赢
        val betReports = betOrderDao.mreport(clientId = null, memberId = null, startDate = startDate)
        val betMap = betReports.groupBy { it.memberId }

        //TODO  Kiss918、Pussy888、Mega
        val otherReports = otherPlatformReportDao.list(startDate = startDate)
                .map { "${it.memberId}_${it.platform}" to it }
                .toMap()


        return list.map { report ->

            // kiss918、pussy、mega平台的日报表
            val otherSettles = listOf(Platform.Kiss918, Platform.Pussy888, Platform.Mega).mapNotNull { platform ->
                val memberId = report.memberId

                otherReports["${memberId}_$platform"]
                        ?.let {
                            MemberDailyReport.PlatformSettle(platform = platform, bet = it.bet, mwin = it.win, validBet = it.bet)
                        }
            }

            // 平台下注金额
            val settles = (betMap[report.memberId]?.map {
                MemberDailyReport.PlatformSettle(platform = it.platform, bet = it.totalBet, mwin = it.totalWin, validBet = it.validBet)
            }?: emptyList()).plus(otherSettles)

            val totalBet = settles.sumByDouble { it.bet.toDouble() }.toBigDecimal().setScale(2, 2) // 总下注金额
            val totalMWin = settles.sumByDouble { it.mwin.toDouble() }.toBigDecimal().setScale(2, 2) // 玩家总盈利金额

            //TODO 返水比例和金额
            val level = levelIds[report.levelId]
                    ?: Level(id = -1, clientId = -1, sportRebate = BigDecimal.ZERO, name = "", liveRebate = BigDecimal.ZERO, slotRebate = BigDecimal.ZERO,
                            fishRebate = BigDecimal.ZERO, status = Status.Normal, createdTime = LocalDateTime.now())
            val rebate = settles.sumByDouble {

                // 公式 (有效打码-优惠金额需要打码) * 游戏平台返水比例
                when (it.platform.category) {
                    PlatformCategory.Fishing ->
                        (it.validBet.minus(report.fishRequirementBet))
                                .multiply(level.fishRebate)
                                .divide(BigDecimal.valueOf(100))
                    PlatformCategory.Slot ->
                        (it.validBet.minus(report.slotRequirementBet))
                                .multiply(level.slotRebate)
                                .divide(BigDecimal.valueOf(100))
                    PlatformCategory.LiveVideo ->
                        (it.validBet.minus(report.liveRequirementBet))
                                .multiply(level.liveRebate)
                                .divide(BigDecimal.valueOf(100))
                    PlatformCategory.Sport ->
                        (it.validBet.minus(report.sportRequirementBet))
                                .multiply(level.sportRebate)
                                .divide(BigDecimal.valueOf(100))
                }.toDouble()
            }.toBigDecimal().setScale(2, 2).let {
                if (it.toDouble() <= 0) BigDecimal.ZERO else it
            }
            val rebateExecution = rebate.setScale(2, 2) == BigDecimal.ZERO.setScale(2, 2)

            report.copy(rebateAmount = rebate, rebateExecution = rebateExecution, totalBet = totalBet, totalMWin = totalMWin, settles = settles)
        }.filter {

            val x = it.isHasData()

            if  (it.memberId == 179) {
                println("11")
            }
            x

        } // 过滤空数据
    }

    override fun startAgentReport(startDate: LocalDate): List<AgentDailyReport> {

        val endDate =  startDate.plusDays(1)
        return analysisDao.agentReport(startDate = startDate, endDate = endDate)
    }

    override fun startAgentMonthReport(agentId: Int?, today: LocalDate): List<AgentMonthReport> {

        val startDate = today.with(TemporalAdjusters.firstDayOfMonth())
        val endDate = today.with(TemporalAdjusters.lastDayOfMonth())

        // 查询代理列表
        val memberQuery = MemberQuery(bossId = null, role = Role.Agent, clientId = null, agentId = null, username = null,
                name = null, phone = null, status = null, levelId = null, promoteCode = null, startTime = null,  endTime = null)
        val agents = memberDao.query(query = memberQuery, current = 0, size = 999999)

        // 代理佣金配置
        val commissions = commissionService.all().sortedBy { it.activeCount }

        // 会员佣金列表
        val memberCollect = analysisDao.agentMonthReport(agentId = null, startDate = startDate, endDate = endDate)
                .map { it.agentId to it }
                .toMap()
        // 会员存活人数
        val memberActives = analysisDao.memberActiveCollect(startDate = startDate, endDate = endDate)
                .map { it.agentId to it }
                .toMap()


        // 下级代理佣金
//        val superiorCollect = analysisDao.agentReportCollect(startDate = startDate, endDate = endDate)
//                .map { it.superiorAgentId to it }
//                .toMap()
        // 下级代理存活
        val superiorActives = analysisDao.agentActiveCollect(startDate = startDate, endDate = endDate)
                .map { it.agentId to it }
                .toMap()

        // 计算所有代理的会员佣金
        val process = agents.filter { it.formal && it.username != "default_agent" }.mapNotNull { agent ->

            try {
//                val agentCommissions = commissions.filter { agent.bossId == it.bossId }.filter { it.type == CommissionType.AgentCommission }

                //TODO 操 有问题？
                val memberCommissions = commissions.filter { agent.bossId == it.bossId }.filter { it.type == CommissionType.MemberCommission }

                // 计算会员佣金
                val memberCommission = memberCollect[agent.id] ?: AgentMonthReport.empty(bossId = agent.bossId, clientId = agent.clientId, agentId = agent.id, day = startDate)
                val memberActive = memberActives[agent.id] ?: AnalysisValue.ActiveCollect(agentId = -1, activeCount = 0)
                val mCommission = memberCommissions.first { it.activeCount > memberActive.activeCount }
                val memberCommissionAmount =
                        (memberCommission.totalBet
                                .minus(memberCommission.totalMWin)
                                .minus(memberCommission.totalRebate)
                                .minus(memberCommission.totalPromotion))
                                .multiply(mCommission.scale)
                                .divide(BigDecimal.valueOf(100))
                                .setScale(2, 2)

                val commissionExecution = memberCommissionAmount.setScale(2, 2) == BigDecimal.ZERO.setScale(2, 2)

                memberCommission.copy(bossId = agent.bossId, clientId = agent.clientId, memberCommission = memberCommissionAmount, memberCommissionScale = mCommission.scale,
                        memberActiveCount = memberActive.activeCount, commissionExecution = commissionExecution, agencyMonthFee = agent.agencyMonthFee, username = agent.username)
            } catch (e: Exception)  {
                log.error("agent month report error: ", e)
                null
            }
        }

        val agentGroup = process.groupBy { it.superiorAgentId }
        val data = process.map { report ->
            when (report.superiorAgentId) {
                -1 -> {
//                    val agentCommission = superiorCollect[agentId]
                    val agentActive = superiorActives[report.superiorAgentId]

                    val agentCommissions = commissions.filter { report.bossId == it.bossId }.filter { it.type == CommissionType.AgentCommission }

                    report.let {
                        val flag  = agentActive != null && agentGroup.containsKey(report.agentId)

                        if (flag) it else null

                    }?.let {
                        val aCommission = agentCommissions.firstOrNull { x -> x.activeCount > agentActive!!.activeCount }

                        if (aCommission != null) aCommission to it else null
                    }?.let { (aCommission, report) ->
                        val sCommissionAmount = agentGroup[report.agentId]!!.sumByDouble { x -> x.memberCommission.toDouble() }.toBigDecimal().setScale(2,  2)
                        val agentCommissionAmount = sCommissionAmount.multiply(aCommission.scale).divide(BigDecimal.valueOf(100))
                                .setScale(2, 2)

                        val commissionExecution = agentCommissionAmount.plus(report.memberCommission).setScale(2, 2) == BigDecimal.ZERO.setScale(2, 2)
                        report.copy(agentCommission = agentCommissionAmount, agentActiveCount = agentActive!!.activeCount, agentCommissionScale = aCommission.scale, commissionExecution = commissionExecution)
                    } ?: report
                }
                else -> report
            }
        }

        return data.filter { agentId == null || it.agentId == agentId }
    }

    override fun startSaleReport(startDate: LocalDate): List<SaleDailyReport> {

        val salesmanList = waiterService.all(role = Role.Sale)

        val query = MemberReportValue.MemberCollectQuery(day = startDate, saleId = null)
        val list = memberDailyReportDao.saleCollect(query)


        // 查询会员数量
        val owmSaleMap = memberDao.saleCount(saleId = null, startDate = startDate, endDate = startDate.plusDays(1), scope = SaleScope.Own)
        val systemSaleMap =  memberDao.saleCount(saleId = null, startDate = startDate, endDate = startDate.plusDays(1), scope = SaleScope.System)

        return salesmanList.map { sale ->

            val saleId = sale.id
            val data = list.filter { it.saleId == saleId }

            val owmMemberCount = owmSaleMap[sale.id]?: 0
            val systemMemberCount = systemSaleMap[sale.id]?: 0

            val ownSaleVo = data.firstOrNull { it.saleScope == SaleScope.Own } ?: MemberReportValue.SaleReportVo.empty(saleScope = SaleScope.Own)
            val systemSaleVo = data.firstOrNull { it.saleScope == SaleScope.System } ?: MemberReportValue.SaleReportVo.empty(saleScope = SaleScope.System)

//            val bossId = if (ownSaleVo.bossId != -1) ownSaleVo.bossId else systemSaleVo.bossId
//            val clientId = if (ownSaleVo.clientId != -1) ownSaleVo.clientId else systemSaleVo.clientId


            val ownCustomerScale = sale.ownCustomerScale
            val systemCustomerScale = sale.systemCustomerScale

            val ownCustomerFee = (ownSaleVo.totalDeposit.minus(ownSaleVo.totalPromotion).minus(ownSaleVo.totalRebate)).multiply(ownCustomerScale).divide(BigDecimal.valueOf(100))
                    .setScale(2, 2)
                    .let {
                        if (it < BigDecimal.ZERO) BigDecimal.ZERO else it
                    }
            val systemCustomerFee = (systemSaleVo.totalDeposit.minus(systemSaleVo.totalPromotion).minus(systemSaleVo.totalRebate)).multiply(systemCustomerScale).divide(BigDecimal.valueOf(100))
                    .setScale(2, 2)
                    .let {
                        if (it < BigDecimal.ZERO) BigDecimal.ZERO else it
                    }
            SaleDailyReport(bossId = sale.bossId, clientId = sale.clientId, saleId = saleId, saleUsername = sale.username, ownCustomerScale = ownCustomerScale, ownCustomerFee = ownCustomerFee,
                    ownTotalDeposit = ownSaleVo.totalDeposit, ownTotalPromotion = ownSaleVo.totalPromotion, ownTotalWithdraw = ownSaleVo.totalWithdraw, ownTotalRebate = ownSaleVo.totalRebate,
                    systemTotalDeposit = systemSaleVo.totalDeposit, systemCustomerFee = systemCustomerFee, systemCustomerScale = systemCustomerScale, systemTotalPromotion = systemSaleVo.totalPromotion,
                    systemTotalRebate = systemSaleVo.totalRebate, systemTotalWithdraw = systemSaleVo.totalWithdraw, id = -1, day = startDate, createdTime = LocalDateTime.now(),
                    ownMemberCount = owmMemberCount, systemMemberCount = systemMemberCount)

        }

    }

    override fun startSaleMonthReport(startDate: LocalDate): List<SaleMonthReport> {
        val endDate = startDate.plusMonths(1)
        return saleDailyReportDao.collect(startDate = startDate, endDate = endDate)
    }

    override fun startClientPlatformReport(startDate: LocalDate): List<ClientPlatformDailyReport> {


        val endDate = startDate.plusDays(1)
        val now = LocalDateTime.now()

        // 转账报表
        val transferQuery = TransferReportQuery(clientId = null, memberId = null, from = null, to = null, startDate = startDate, endDate = endDate)
        val transferReports = transferOrderDao.clientPlatformReport(query = transferQuery)
        val data = transferReports.groupBy { "${it.clientId}:${it.platform}" }.map { maps ->

            val transferInReport = maps.value.find { it.from == Platform.Center }
            val transferOutReport = maps.value.find { it.to == Platform.Center }

            val first = maps.value.first()
            val platform = if (first.from == Platform.Center) first.to else first.from
            ClientPlatformDailyReport(id = -1, clientId = first.clientId, day = startDate, platform = platform,
                    transferIn = transferInReport?.money ?: BigDecimal.ZERO, transferOut = transferOutReport?.money ?: BigDecimal.ZERO, createdTime = now,
                    win = BigDecimal.valueOf(-1), bet = BigDecimal.valueOf(-1), promotionAmount = transferOutReport?.promotionAmount ?: BigDecimal.ZERO,
                    status = Status.Normal, activeCount = 0)
        }

        // 盈利报表
        val betOrderReports = betOrderService.report(startDate = startDate, endDate = endDate)


        // 组合
        val transferKeys = data.map { "${it.clientId}:${it.platform}" to it }.toMap()
        val betOrderKeys = betOrderReports.map { "${it.clientId}:${it.platform}" to it }.toMap()
        val keys = transferKeys.keys.plus(betOrderKeys.keys)

        // 转账存活人数
        val activeCountMap = transferOrderDao.queryActiveCount(startDate = startDate, endDate = endDate)
                .map { "${it.clientId}:${it.platform}" to it.count }
                .toMap()
        log.info("--------转账存活人数---------")
        log.info("--------转账存活人数---------")
        log.info("$activeCountMap")
        log.info("--------转账存活人数---------")
        log.info("--------转账存活人数---------")


        // 组合数据
        return keys.map { key ->

            val transferData = transferKeys[key]
            val betOrderData = betOrderKeys[key]
            val activeCount  = activeCountMap[key]?: 0

            when {
                transferData == null -> {
                    ClientPlatformDailyReport(id = -1, day = startDate, platform = betOrderData!!.platform, bet = betOrderData.totalBet, win = betOrderData.totalWin,
                            transferIn = BigDecimal.ZERO, transferOut = BigDecimal.ZERO, clientId = betOrderData.clientId, createdTime = now, promotionAmount = BigDecimal.ZERO,
                            status = Status.Normal, activeCount = activeCount)
                }
                betOrderData == null -> {
                    when(transferData.platform) {
                        Platform.Kiss918,
                        Platform.Pussy888,
                        Platform.Mega -> {
                            transferData.copy(bet = BigDecimal.valueOf(-1), win = BigDecimal.valueOf(-1), activeCount = activeCount)
                        }
                        else -> transferData.copy(bet = BigDecimal.ZERO, win = BigDecimal.ZERO, activeCount = activeCount)
                    }
                }
                else -> {
                    transferData.copy(bet = betOrderData.totalBet, win = betOrderData.totalWin, activeCount = activeCount)

                }
            }
        }
    }

    override fun startClientReport(startDate: LocalDate): List<ClientDailyReport> {
        val endDate = startDate.plusDays(1)
        val list =  analysisDao.clientReport(startDate = startDate, endDate = endDate)

        val map = analysisDao.activeCount(startDate = startDate, endDate = startDate.plusDays(1))

        return list.map {
            val activeCount = map[it.clientId] ?: 0
            it.copy(activeCount = activeCount)
        }
    }
}
