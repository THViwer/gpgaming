package com.onepiece.treasure.task

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.ClientDailyReport
import com.onepiece.treasure.beans.model.ClientPlatformDailyReport
import com.onepiece.treasure.beans.model.MemberDailyReport
import com.onepiece.treasure.beans.model.MemberPlatformDailyReport
import com.onepiece.treasure.core.service.*
import com.onepiece.treasure.games.GamePlatformUtil
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class ReportTask(
        private val memberPlatformDailyReportService: MemberPlatformDailyReportService,
        private val memberDailyReportService: MemberDailyReportService,
        private val clientPlatformDailyReportService: ClientPlatformDailyReportService,
        private val clientDailyReportService: ClientDailyReportService,

        private val depositService: DepositService,
        private val withdrawService: WithdrawService,

        private val transferOrderService: TransferOrderService,
        private val gamePlatformUtil: GamePlatformUtil
) {


    // 会员平台日报表
    fun startMemberPlatformDailyReport(startDate: LocalDate) {

        val endDate = startDate.plusDays(1)

        val transferReports = transferOrderService.report(startDate, endDate).map {
            "${it.clientId}_${it.memberId}_${it.from}_${it.to}" to it
        }.toMap()

        val now = LocalDateTime.now()
        Platform.all().map { platform ->
            val data = gamePlatformUtil.getPlatformBuild(platform).gameOrderApi.report(startDate = startDate, endDate = endDate)
            if (data.isNotEmpty()) {
                val reports = data.map {

                    val transferInKey = "${it.clientId}_${it.memberId}_${Platform.Center}_${platform}"
                    val transferOutKey = "${it.clientId}_${it.memberId}_${platform}_${Platform.Center}"

                    val transferInMoney = transferReports[transferInKey]?.money ?: BigDecimal.ZERO
                    val transferOutMoney = transferReports[transferOutKey]?.money ?: BigDecimal.ZERO

                    MemberPlatformDailyReport(id = -1, day = startDate, clientId = it.clientId, memberId = it.memberId, platform = platform,
                            bet = it.bet, win = it.win, createdTime = now, transferIn = transferInMoney, transferOut = transferOutMoney)
                }
                memberPlatformDailyReportService.create(reports)

            }
        }
    }


    // 会员日报表
    fun startMemberReport(startDate: LocalDate) {

        val endDate = startDate.plusDays(1)

        val depositReports = depositService.report(startDate, endDate)
                .map { it.memberId to it }.toMap()
        val withdrawReports = withdrawService.report(startDate, endDate)
                .map { it.memberId to it }.toMap()


        val now = LocalDateTime.now()
        val reports = memberPlatformDailyReportService.report(startDate, endDate).map {

            val deposit = depositReports[it.memberId]?.money?: BigDecimal.ZERO
            val withdraw = withdrawReports[it.memberId]?.money?: BigDecimal.ZERO

            MemberDailyReport(id = -1, day = startDate, clientId = it.clientId, memberId = it.memberId, bet = it.bet, win = it.win,
                    transferIn = it.transferIn, transferOut = it.transferOut, depositMoney = deposit, withdrawMoney = withdraw,
                    createdTime = now)
        }
        memberDailyReportService.create(reports)
    }

    // 厅主平台日报表
    fun startClientPlatformReport(startDate: LocalDate) {
        val endDate = startDate.plusDays(1)

        val transferReports = transferOrderService.reportByClient(startDate, endDate).map {
            "${it.clientId}_${it.from}_${it.to}" to it
        }.toMap()

        val now = LocalDateTime.now()

        val reports = memberPlatformDailyReportService.reportByClient(startDate, endDate).map {
            val transferInKey = "${it.clientId}_${Platform.Center}_${it.platform}"
            val transferOutKey = "${it.clientId}_${it.platform}_${Platform.Center}"

            val transferIn = transferReports[transferInKey]?.money?: BigDecimal.ZERO
            val transferOut = transferReports[transferOutKey]?.money?: BigDecimal.ZERO


            ClientPlatformDailyReport(id = -1, day = startDate, clientId = it.clientId, platform = it.platform, bet = it.bet, win = it.win,
                    transferIn = transferIn, transferOut = transferOut, createdTime = now)

        }

        clientPlatformDailyReportService.create(reports)

    }


    // 厅主报表
    fun startClientReport(startDate: LocalDate) {
        val endDate = startDate.plusDays(1)

        val depositReports = depositService.reportByClient(startDate, startDate.plusDays(1))
                .map { it.clientId to it }.toMap()
        val withdrawReports = withdrawService.reportByClient(startDate, startDate.plusDays(1))
                .map { it.clientId to it }.toMap()


        val now = LocalDateTime.now()
        val reports = clientPlatformDailyReportService.report(startDate, endDate).map {
            ClientDailyReport(id = -1, day = startDate, bet = it.bet, win = it.win, transferIn = it.transferIn, transferOut = it.transferOut,
                    depositMoney = depositReports[it.clientId]?.money?: BigDecimal.ZERO, depositCount = depositReports[it.clientId]?.count?: 0,
                    withdrawMoney = withdrawReports[it.clientId]?.money?: BigDecimal.ZERO, withdrawCount = withdrawReports[it.clientId]?.count?: 0,
                    createdTime = now, clientId = it.clientId)
        }

        clientDailyReportService.create(reports)
    }

}