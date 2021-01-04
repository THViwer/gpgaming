package com.onepiece.gpgaming.core.daily

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.MemberDailyReport
import com.onepiece.gpgaming.beans.value.database.MemberReportQuery
import com.onepiece.gpgaming.core.dao.BetOrderDao
import com.onepiece.gpgaming.core.dao.DepositDao
import com.onepiece.gpgaming.core.dao.PayOrderDao
import com.onepiece.gpgaming.core.dao.TransferOrderDao
import com.onepiece.gpgaming.core.dao.TransferReportQuery
import com.onepiece.gpgaming.core.dao.WithdrawDao
import com.onepiece.gpgaming.core.service.MemberDailyReportService
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class MemberTodayDetailUtil(
        private val depositDao: DepositDao,
        private val payOrderDao: PayOrderDao,

        private val totalWithdrawDao: WithdrawDao,

        private val transferOrderDao: TransferOrderDao,

        private val betOrderDao: BetOrderDao,

        private val memberDailyReportService: MemberDailyReportService
) {

    fun getMemberDetails(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): MemberDailyDetail {

        val history = this.getHistory(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate)

        val list = if (endDate >= LocalDate.now()) {
            val today = getToday(clientId = clientId, memberId = memberId)
            history.plus(today)
        } else history


        val totalDeposit = list.sumByDouble { it.totalDeposit.toDouble() }.toBigDecimal().setScale(2, 2)
        val totalDepositFrequency = list.sumBy { it.totalDepositFrequency }
        val totalWithdraw = list.sumByDouble { it.totalWithdraw.toDouble() }.toBigDecimal().setScale(2, 2)
        val totalPromotion = list.sumByDouble { it.totalPromotion.toDouble() }.toBigDecimal().setScale(2, 2)

        val settles = list.map { it.settles }.reduce { acc, _list -> acc.plus(_list) }
                .groupBy { it.platform }
                .map {
                    val vs = it.value

                    val bet = vs.sumByDouble { x -> x.bet.toDouble() }.toBigDecimal().setScale(2, 2)
                    val validBet = vs.sumByDouble { x -> x.validBet.toDouble() }.toBigDecimal().setScale(2, 2)
                    val payout = vs.sumByDouble { x -> x.payout.toDouble() }.toBigDecimal().setScale(2, 2)
                    val rebate = vs.sumByDouble { x -> x.rebate.toDouble() }.toBigDecimal().setScale(2, 2)
                    val requirementBet = vs.sumByDouble { x -> x.requirementBet.toDouble() }.toBigDecimal().setScale(2, 2)
                    val totalIn = vs.sumByDouble { x -> x.totalIn.toDouble() }.toBigDecimal().setScale(2, 2)
                    val totalOut = vs.sumByDouble { x -> x.totalOut.toDouble() }.toBigDecimal().setScale(2, 2)

                    MemberDailyReport.PlatformSettle(platform = it.key, bet = bet, validBet = validBet, payout = payout, rebate = rebate,
                            requirementBet = requirementBet, totalIn = totalIn, totalOut = totalOut)
                }

        return MemberDailyDetail(memberId = memberId, totalDeposit = totalDeposit, totalDepositFrequency = totalDepositFrequency, totalWithdraw = totalWithdraw,
                totalWithdrawFrequency = totalDepositFrequency, totalPromotion = totalPromotion, settles = settles)
    }

    fun getHistory(clientId: Int, memberId: Int, startDate: LocalDate, endDate: LocalDate): List<MemberDailyDetail> {

        val reportQuery = MemberReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate, agentId = null, current = 0,
                size = 999999, minPromotionAmount = null, minRebateAmount = null)
        val history = memberDailyReportService.query(reportQuery)

        return history.map { report ->
            MemberDailyDetail(memberId = report.memberId, totalDeposit = report.depositAmount, totalDepositFrequency = report.depositCount,
                    totalWithdraw = report.withdrawAmount, totalWithdrawFrequency = report.withdrawCount, totalPromotion = report.promotionAmount,
                    settles = report.settles)
        }
    }


    fun getToday(clientId: Int, memberId: Int): MemberDailyDetail {

        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(1)

        var detail = MemberDailyDetail(memberId = memberId)

        //1. 查询今日充值
        depositDao.report(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate).firstOrNull()?.let {
            detail = detail.copy(totalDeposit = it.money, totalDepositFrequency = it.count)
        }
        payOrderDao.mReport(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate).firstOrNull()?.let {
            val totalDeposit = detail.totalDeposit.plus(it.totalAmount)
            val totalDepositFrequency = detail.totalDepositFrequency.plus(it.count)
            detail = detail.copy(totalDeposit = totalDeposit, totalDepositFrequency = totalDepositFrequency)
        }

        //2. 查询今日出款
        totalWithdrawDao.report(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate).firstOrNull()?.let {
            detail = detail.copy(totalWithdraw = it.money, totalWithdrawFrequency = it.count)
        }

        //3. 查询转账
        val transferQuery = TransferReportQuery(clientId = clientId, memberId = memberId, startDate = startDate, endDate = endDate, from = null, to = null)
        val transferReports = transferOrderDao.memberPlatformReport(transferQuery)
                .let {
//                    var totalPromotionFrequency = 0
                    val totalPromotion = it.sumByDouble { x ->
//                        if (x.promotionAmount.toDouble() > 0) totalPromotionFrequency += 1
                        x.promotionAmount.toDouble()
                    }.toBigDecimal().setScale(2, 2)
                    detail = detail.copy(totalPromotion = totalPromotion)
//                    detail = detail.copy(totalPromotion = totalPromotion, totalPromotionFrequency = totalPromotionFrequency)
                    it
                }
                .map { "${it.from}:${it.to}" to it }
                .toMap()

        //4. 查询下注
        val betReports = betOrderDao.mreport(clientId = clientId, memberId = memberId, startDate = startDate)
                .map { it.platform to it }
                .toMap()


        // 组装平台数据
        val settles = Platform.values().map { platform ->

            val transferInReport = transferReports["$platform:${Platform.Center}"]
            val transferOutReport = transferReports["${Platform.Center}:${Platform}"]

            val betReport = betReports[platform]

            var settle = MemberDailyReport.PlatformSettle(platform = platform)

            if (transferInReport != null) {
                settle = settle.copy(totalIn = transferInReport.money)
            }

            if (transferOutReport != null) {
                settle = settle.copy(totalOut = transferOutReport.money)
            }

            if (betReport != null) {
                settle = settle.copy(bet = betReport.totalBet, validBet = betReport.validBet, payout = betReport.payout)
            }

            settle
        }

        // 组装今日数据
        return detail.copy(settles = settles)
    }

}