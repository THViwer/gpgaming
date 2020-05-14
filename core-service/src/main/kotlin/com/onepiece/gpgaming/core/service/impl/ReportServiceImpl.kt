package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.PlatformCategory
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.ClientDailyReport
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.model.Level
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.model.MemberPlatformDailyReport
import com.onepiece.gpgaming.core.dao.ArtificialOrderDao
import com.onepiece.gpgaming.core.dao.BetOrderDao
import com.onepiece.gpgaming.core.dao.DepositDao
import com.onepiece.gpgaming.core.dao.LevelDao
import com.onepiece.gpgaming.core.dao.MemberDailyReportDao
import com.onepiece.gpgaming.core.dao.MemberDao
import com.onepiece.gpgaming.core.dao.PayOrderDao
import com.onepiece.gpgaming.core.dao.TransferOrderDao
import com.onepiece.gpgaming.core.dao.TransferReportQuery
import com.onepiece.gpgaming.core.dao.WithdrawDao
import com.onepiece.gpgaming.core.service.BetOrderService
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.ReportService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
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
        private val betOrderDao: BetOrderDao,
        private val payOrderDao: PayOrderDao,
        private val memberDailyReportDao: MemberDailyReportDao,
        private val clientService: ClientService,
        private val levelDao: LevelDao
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

        val queryClientId = if (memberId != null) memberDao.get(memberId).clientId else  null

        // 查询转入订单
        val transferInQuery = TransferReportQuery(clientId = queryClientId, memberId = memberId, from = Platform.Center, to = null, startDate = startDate, endDate = endDate)
        val transferInReports = transferOrderDao.memberPlatformReport(transferInQuery)
        val transferInMap = transferInReports.groupBy { it.memberId }.toMap()

        // 查询转出订单
        val transferOutQuery = TransferReportQuery(clientId = queryClientId, memberId = memberId, from = null, to = Platform.Center, startDate = startDate, endDate = endDate)
        val transferOutReports = transferOrderDao.memberReport(transferOutQuery)
        val transferOutMap = transferOutReports.map { it.memberId to it }.toMap()

        // 充值报表
        val depositReports = depositDao.report(clientId = queryClientId, memberId = memberId, startDate = startDate, endDate = endDate)
        val depositReportMap = depositReports.map { it.memberId to it }.toMap()

        // 取款报表
        val withdrawReports = withdrawDao.report(clientId = queryClientId, memberId = memberId, startDate = startDate, endDate = endDate)
        val withdrawReportMap = withdrawReports.map { it.memberId to it }.toMap()

        // 人工提存
        val mArtificialReports = artificialOrderDao.mReport(clientId = queryClientId, memberId = memberId, startDate = startDate)
        val mArtificialReportMap = mArtificialReports.map { it.memberId to it }.toMap()

        // 平台报表 输赢
        val betReports = betOrderDao.mreport(clientId = queryClientId, memberId = memberId, startDate = startDate)
        val betMap = betReports.groupBy { it.memberId }

        // 第三方充值
        val payOrders = payOrderDao.mReport(clientId = queryClientId, startDate = startDate, endDate = endDate, memberId = memberId)
        val payOrderMap = payOrders.map { it.memberId to it }.toMap()

        // 会员对应返水比例
        val levelIds = levelDao.all().map { it.id to it }.toMap()

        val members = memberId?.let { listOf(memberDao.get(it)) } ?: memberDao.all()
        return members.map { member ->
            val mid = member.id


//            val transferInReport = transferInMap[mid]
//            val transferIn = transferInReport?.money ?: BigDecimal.ZERO // 转入金额
//            val promotionMoney = transferInReport?.promotionAmount ?: BigDecimal.ZERO // 优惠金额
            val transferIns = transferInMap[mid]?: emptyList()
            val transferIn = transferIns.sumByDouble { it.money.toDouble() }.toBigDecimal().setScale(2, 2)
            val promotionMoney = transferIns.sumByDouble { it.promotionAmount.toDouble() }.toBigDecimal().setScale(2, 2)

            val transferOutReport = transferOutMap[mid]
            val transferOut = transferOutReport?.money ?: BigDecimal.ZERO // 转出金额

            val depositReport = depositReportMap[mid]
            val depositMoney = depositReport?.money ?: BigDecimal.ZERO // 存款金额
            val depositCount = depositReport?.count?:0 // 存款次数


            val withdrawReport = withdrawReportMap[mid]
            val withdrawMoney = withdrawReport?.money ?: BigDecimal.ZERO // 取款金额
            val withdrawCount = withdrawReport?.count?: 0 // 取款次数


            val artificialReport = mArtificialReportMap[mid]
            val artificialMoney = artificialReport?.totalAmount?: BigDecimal.ZERO // 人工提存金额
            val artificialCount = artificialReport?.count?: 0 // 人工提存次数

            val payOrder = payOrderMap[mid]
            val thirdPayMoney = payOrder?.totalAmount ?: BigDecimal.ZERO // 第三方充值金额
            val thirdPayCount = payOrder?.count?: 0 // 第三方充值次数

            // 平台下注金额
            val settles = betMap[mid]?.map {
                MemberDailyReport.PlatformSettle(platform = it.platform, bet = it.totalBet, mwin = it.totalWin, validBet = it.validBet)
            }?: emptyList()
            val totalBet = settles.sumByDouble { it.bet.toDouble() }.toBigDecimal().setScale(2, 2) // 总下注金额
            val totalMWin = settles.sumByDouble { it.mwin.toDouble() }.toBigDecimal().setScale(2, 2) // 玩家总盈利金额

            //TODO 返水比例和金额
            val level = levelIds[member.levelId]
                    ?: Level(id = -1, clientId = -1, sportRebate = BigDecimal.ZERO, name = "", liveRebate = BigDecimal.ZERO, slotRebate = BigDecimal.ZERO,
                            fishRebate = BigDecimal.ZERO, status = Status.Normal, createdTime = LocalDateTime.now())
            val rebate = settles.sumByDouble {

                // 公式 (有效打码-优惠金额需要打码) * 游戏平台返水比例
                val requirementBet = transferIns.firstOrNull{ x -> it.platform == x.platform}?.requirementBet?: BigDecimal.ZERO
                val validBet = it.validBet.minus(requirementBet)
                when (it.platform.category) {
                    PlatformCategory.Fishing -> validBet.multiply(level.fishRebate).divide(BigDecimal.valueOf(100))
                    PlatformCategory.Slot -> validBet.multiply(level.slotRebate).divide(BigDecimal.valueOf(100))
                    PlatformCategory.LiveVideo -> validBet.multiply(level.liveRebate).divide(BigDecimal.valueOf(100))
                    PlatformCategory.Sport -> validBet.multiply(level.sportRebate).divide(BigDecimal.valueOf(100))
                }.toDouble()
            }.toBigDecimal().setScale(2, 2).let {
                if (it.toDouble() <= 0) BigDecimal.ZERO else it
            }

            val rebateExecution = rebate.setScale(2, 2) == BigDecimal.ZERO.setScale(2, 2)

            val empty = transferIn.plus(transferOut).plus(depositMoney).plus(withdrawMoney).plus(artificialMoney).plus(thirdPayMoney)
                    .plus(totalBet)

            if (empty.setScale(2, 2) == BigDecimal.ZERO.setScale(2, 2)) {
                null
            } else {
                MemberDailyReport(id = -1, day = startDate, clientId = member.clientId, memberId = mid, transferIn = transferIn,
                        transferOut = transferOut, depositMoney = depositMoney, withdrawMoney = withdrawMoney, depositCount = depositCount, withdrawCount = withdrawCount,
                        artificialMoney = artificialMoney,  artificialCount = artificialCount, settles = settles, totalMWin = totalMWin, totalBet = totalBet ,
                        thirdPayMoney = thirdPayMoney, thirdPayCount = thirdPayCount, backwaterMoney = rebate, createdTime = now,
                        status = Status.Normal, backwaterExecution = rebateExecution, promotionMoney = promotionMoney)
            }
        }.filterNotNull()

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
        val artificialReports = artificialOrderDao.cReport(startDate = startDate)
        val artificialReportMap = artificialReports.map { it.clientId to it }.toMap()

        // 查询输赢
        val betReports  = betOrderDao.creport(startDate = startDate)
        val betMap = betReports.map { it.clientId to it }.toMap()

        // 第三方充值
        val payOrders = payOrderDao.cReport(startDate = startDate, constraint = false)
        val payOrderMap = payOrders.map { it.clientId to it }.toMap()

        // 返水金额
        val backwaterMap = memberDailyReportDao.backwater(startDate = startDate)

        //TODO 第三方充值强制入款
        val clients = clientService.all().filter { clientId == null || it.id == clientId }

//        val clientIds = transferInReports.asSequence().map { it.clientId }
//                .plus(transferOutReports.map { it.clientId })
//                .plus(depositReports.map { it.clientId })
//                .plus(withdrawReports.map { it.clientId })
//                .plus(artificialReports.map { it.clientId })
//                .plus(betReports.map { it.clientId })
//                .plus(payOrders.map { it.clientId })
//                .toSet()

        return clients.map { it.id }.map {

            val transferInReport = transferInMap[it]
            val transferIn = transferInReport?.transferIn ?: BigDecimal.ZERO

            val transferOutReport = transferOutMap[it]
            val transferOut = transferOutReport?.transferOut ?: BigDecimal.ZERO

            val depositReport = depositReportMap[it]
            val depositMoney = depositReport?.money ?: BigDecimal.ZERO
            val depositCount = depositReport?.count ?: 0
            val depositSequence = depositReport?.depositSequence?: 0

            val withdrawReport = withdrawReportMap[it]
            val withdrawMoney = withdrawReport?.money ?: BigDecimal.ZERO
            val withdrawCount = withdrawReport?.count ?: 0

            val newMemberCount = memberReportMap[it]?: 0

            val artificialReport = artificialReportMap[it]
            val artificialMoney = artificialReport?.totalAmount?: BigDecimal.ZERO
            val artificialCount = artificialReport?.count?: 0

            val payOrder = payOrderMap[it]
            val thirdPayMoney = payOrder?.totalAmount?: BigDecimal.ZERO
            val thirdPayCount = payOrder?.count?: 0
            val thirdPaySequence = payOrder?.thirdPaySequence?: 0

            val promotionAmount = transferInReport?.promotionAmount?: BigDecimal.ZERO

            val totalBet = betMap[it]?.totalBet?: BigDecimal.ZERO
            val totalMWin = betMap[it]?.totalWin?: BigDecimal.ZERO

            val backwater = backwaterMap[it] ?: BigDecimal.ZERO



            ClientDailyReport(id = -1, day = startDate, clientId = it, transferIn = transferIn,
                    transferOut = transferOut, depositMoney = depositMoney,
                    depositCount = depositCount, withdrawMoney = withdrawMoney,
                    withdrawCount = withdrawCount, newMemberCount = newMemberCount, createdTime = now,
                    artificialMoney = artificialMoney, artificialCount = artificialCount,
                    promotionAmount = promotionAmount, status = Status.Normal, totalBet = totalBet,
                    totalMWin = totalMWin, thirdPayMoney = thirdPayMoney,
                    thirdPayCount = thirdPayCount, depositSequence = depositSequence,
                    thirdPaySequence = thirdPaySequence, backwaterMoney = backwater)
        }
    }
}
