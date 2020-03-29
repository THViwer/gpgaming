package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.model.MemberPlatformDailyReport
import com.onepiece.gpgaming.core.dao.ArtificialOrderDao
import com.onepiece.gpgaming.core.dao.BetOrderDao
import com.onepiece.gpgaming.core.dao.DepositDao
import com.onepiece.gpgaming.core.dao.MemberDao
import com.onepiece.gpgaming.core.dao.TransferOrderDao
import com.onepiece.gpgaming.core.dao.TransferReportQuery
import com.onepiece.gpgaming.core.dao.WithdrawDao
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.core.service.ReportService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Exception
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ReportServiceImpl(
        private val transferOrderDao: TransferOrderDao,
        private val depositDao: DepositDao,
        private val withdrawDao: WithdrawDao,
        private val memberDao: MemberDao,
        private val betOrderService: BetOrderService,
        private val artificialOrderDao: ArtificialOrderDao,
        private val betOrderDao: BetOrderDao
) : ReportService {

    private val log = LoggerFactory.getLogger(ReportServiceImpl::class.java)

    override fun startMemberPlatformDailyReport(memberId: Int?, startDate: LocalDate): List<MemberPlatformDailyReport> {

        val endDate = startDate.plusDays(1)
        val now = LocalDateTime.now()

        // 转账报表
        val transferQuery = TransferReportQuery(clientId = null, memberId = memberId, from = null, to = null, startDate = startDate, endDate = endDate)
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

    override fun startMemberReport(memberId: Int?, startDate: LocalDate): List<MemberDailyReport> {
        val endDate = startDate.plusDays(1)
        val now = LocalDateTime.now()

        // 查询转入订单
        val transferInQuery = TransferReportQuery(clientId = null, memberId = memberId, from = Platform.Center, to = null, startDate = startDate, endDate = endDate)
        val transferInReports = transferOrderDao.memberReport(transferInQuery)
        val transferInMap = transferInReports.map { it.memberId to it }.toMap()

        // 查询转出订单
        val transferOutQuery = TransferReportQuery(clientId = null, memberId = memberId, from = null, to = Platform.Center, startDate = startDate, endDate = endDate)
        val transferOutReports = transferOrderDao.memberReport(transferOutQuery)
        val transferOutMap = transferOutReports.map { it.memberId to it }.toMap()

        // 充值报表
        val depositReports = depositDao.report(startDate = startDate, endDate = endDate)
        val depositReportMap = depositReports.map { it.memberId to it }.toMap()

        // 取款报表
        val withdrawReports = withdrawDao.report(startDate = startDate, endDate = endDate)
        val withdrawReportMap = withdrawReports.map { it.memberId to it }.toMap()

        // 人工提存
        val mArtificialReports = artificialOrderDao.mReport(startDate = startDate)
        val mArtificialReportMap = mArtificialReports.map { it.memberId to it }.toMap()

        // 平台报表
        val betReports = betOrderDao.mreport(startDate = startDate)
        val betMap = betReports.groupBy { it.memberId }


        val memberIdSet = transferInReports.asSequence().map { it.memberId }
                .plus(transferOutReports.map { it.memberId })
                .plus(depositReports.map { it.memberId })
                .plus(withdrawReports.map { it.memberId })
                .plus(mArtificialReports.map { it.memberId })
                .plus(betReports.map { it.memberId })
                .toSet()

        return memberIdSet.map {

            val transferInReport = transferInMap[it]
            val transferOutReport = transferOutMap[it]
            val depositReport = depositReportMap[it]
            val withdrawReport = withdrawReportMap[it]
            val artificialReport = mArtificialReportMap[it]

            val settles = betMap[it]?.map {
                    MemberDailyReport.PlatformSettle(platform = it.platform, bet = it.totalBet, mwin = it.totalWin)
            }?: emptyList()

            val clientId = when {
                memberId != null -> memberDao.get(memberId).clientId
                transferInReport != null -> transferInReport.clientId
                transferOutReport != null -> transferOutReport.clientId
                depositReport != null -> depositReport.clientId
                withdrawReport != null -> withdrawReport.clientId
                betMap[it] != null-> (betMap[it] ?: error("betMap error")).first().clientId
                else -> error(OnePieceExceptionCode.DATA_FAIL)
            }
            MemberDailyReport(id = -1, day = startDate, clientId = clientId, memberId = it, transferIn = transferInReport?.money ?: BigDecimal.ZERO,
                    transferOut = transferOutReport?.money ?: BigDecimal.ZERO, depositMoney = depositReport?.money ?: BigDecimal.ZERO,
                    withdrawMoney = withdrawReport?.money ?: BigDecimal.ZERO, createdTime = now, status = Status.Normal, depositCount = depositReport?.count?:0,
                    withdrawCount = withdrawReport?.count?: 0, artificialMoney = artificialReport?.totalAmount?: BigDecimal.ZERO, artificialCount = artificialReport?.count?: 0,
                    settles = settles, totalMWin = settles.sumByDouble { it.bet.toDouble() }.toBigDecimal().setScale(2, 2),
                    totalBet = settles.sumByDouble { it.cwin.toDouble() }.toBigDecimal().setScale(2, 2))
        }
    }

    override fun startClientPlatformReport(clientId: Int?, startDate: LocalDate): List<ClientPlatformDailyReport> {


        val endDate = startDate.plusDays(1)
        val now = LocalDateTime.now()

        // 转账报表
        val transferQuery = TransferReportQuery(clientId = clientId, memberId = null, from = null, to = null, startDate = startDate, endDate = endDate)
        val transferReports = transferOrderDao.clientPlatformReport(query = transferQuery)
        val data = transferReports.groupBy { "${it.clientId}:${it.platform}" }.map { maps ->

            val transferInReport = maps.value.find { it.from == Platform.Center }
            val transferOutReport = maps.value.find { it.to == Platform.Center }

            val first = maps.value.first()
            val platform = if (first.from == Platform.Center) first.to else first.from
            ClientPlatformDailyReport(id = -1, clientId = first.clientId, day = startDate, platform = platform,
                    transferIn = transferInReport?.money ?: BigDecimal.ZERO, transferOut = transferOutReport?.money ?: BigDecimal.ZERO, createdTime = now,
                    win = BigDecimal.valueOf(-1), bet = BigDecimal.valueOf(-1), promotionAmount = transferOutReport?.promotionAmount ?: BigDecimal.ZERO,
                    status = Status.Normal)
        }

        // 盈利报表
        val betOrderReports = betOrderService.report(startDate = startDate, endDate = endDate)


        // 组合
        val transferKeys = data.map { "${it.clientId}:${it.platform}" to it }.toMap()
        val betOrderKeys = betOrderReports.map { "${it.clientId}:${it.platform}" to it }.toMap()
        val keys = transferKeys.keys.plus(betOrderKeys.keys)

        // 组合数据
        return keys.map { key ->

            val transferData = transferKeys[key]
            val betOrderData = betOrderKeys[key]

            when {
                transferData == null -> {
                    ClientPlatformDailyReport(id = -1, day = startDate, platform = betOrderData!!.platform, bet = betOrderData.totalBet, win = betOrderData.totalWin,
                            transferIn = BigDecimal.ZERO, transferOut = BigDecimal.ZERO, clientId = betOrderData.clientId, createdTime = now, promotionAmount = BigDecimal.ZERO,
                            status = Status.Normal)
                }
                betOrderData == null -> {
                    when(transferData.platform) {
                        Platform.Kiss918,
                        Platform.Pussy888,
                        Platform.Mega -> {
                            transferData.copy(bet = BigDecimal.valueOf(-1), win = BigDecimal.valueOf(-1))
                        }
                        else -> transferData.copy(bet = BigDecimal.ZERO, win = BigDecimal.ZERO)
                    }
                }
                else -> {
                    transferData.copy(bet = betOrderData.totalBet, win = betOrderData.totalWin)

                }
            }
        }
    }

    override fun startClientReport(clientId: Int?, startDate: LocalDate): List<ClientDailyReport> {
        val endDate = startDate.plusDays(1)
        val now = LocalDateTime.now()

        // 查询转入订单
        val transferInQuery = TransferReportQuery(clientId = clientId, memberId = null, from = Platform.Center, to = null, startDate = startDate, endDate = endDate)
        val transferInReports = transferOrderDao.clientReport(transferInQuery)
        val transferInMap = transferInReports.map { it.clientId to it }.toMap()

        // 查询转出订单
        val transferOutQuery = TransferReportQuery(clientId = clientId, memberId = null, from = null, to = Platform.Center, startDate = startDate, endDate = endDate)
        val transferOutReports = transferOrderDao.clientReport(transferOutQuery)
        val transferOutMap = transferOutReports.map { it.clientId to it }.toMap()


        // 充值报表
        val depositReports = depositDao.reportByClient(startDate = startDate, endDate = endDate)
        val depositReportMap = depositReports.map { it.clientId to it }.toMap()

        // 取款报表
        val withdrawReports = withdrawDao.reportByClient(startDate = startDate, endDate = endDate)
        val withdrawReportMap = withdrawReports.map { it.clientId to it }.toMap()

        // 会员报表
        val memberReportMap = memberDao.report(clientId = clientId, startDate = startDate, endDate = endDate)

        // 人工提存报表
        val artificialReports = artificialOrderDao.mReport(startDate = startDate)
        val artificialReportMap = artificialReports.map { it.clientId to it }.toMap()

        // 查询输赢
        val betReports  = betOrderDao.creport(startDate = startDate)
        val betMap = betReports.map { it.clientId to it }
                .toMap()

        val clientIds = transferInReports.asSequence().map { it.clientId }
                .plus(transferOutReports.map { it.clientId })
                .plus(depositReports.map { it.clientId })
                .plus(withdrawReports.map { it.clientId })
                .plus(artificialReports.map { it.clientId })
                .plus(betReports.map { it.clientId })
                .toSet()

        return clientIds.map {

            val transferInReport = transferInMap[it]
            val transferOutReport = transferOutMap[it]
            val depositReport = depositReportMap[it]
            val withdrawReport = withdrawReportMap[it]
            val newMemberCount = memberReportMap[it]?: 0
            val artificialReport = artificialReportMap[it]

            val promotionAmount = transferOutReport?.promotionAmount?: BigDecimal.ZERO

            ClientDailyReport(id = -1, day = startDate, clientId = it, transferIn = transferInReport?.transferIn ?: BigDecimal.ZERO,
                    transferOut = transferOutReport?.transferOut ?: BigDecimal.ZERO, depositMoney = depositReport?.money ?: BigDecimal.ZERO,
                    depositCount = depositReport?.count ?: 0, withdrawMoney = withdrawReport?.money ?: BigDecimal.ZERO,
                    withdrawCount = withdrawReport?.count ?: 0, newMemberCount = newMemberCount, createdTime = now,
                    artificialMoney = artificialReport?.totalAmount?: BigDecimal.ZERO, artificialCount = artificialReport?.count?: 0,
                    promotionAmount = promotionAmount, status = Status.Normal, totalBet = betMap[it]?.totalBet?: BigDecimal.ZERO,
                    totalMWin = betMap[it]?.totalWin?: BigDecimal.ZERO)
        }
    }
}
