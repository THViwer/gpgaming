package com.onepiece.treasure.task

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.ClientDailyReport
import com.onepiece.treasure.beans.model.ClientPlatformDailyReport
import com.onepiece.treasure.beans.model.MemberDailyReport
import com.onepiece.treasure.beans.model.MemberPlatformDailyReport
import com.onepiece.treasure.core.service.*
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

        private val transferOrderService: TransferOrderService
//        private val gamePlatformUtil: GamePlatformUtil,
//        private val kiss918GameReportApi: GameReportApi,
//        private val platformBindService: PlatformBindService
) {

    // 会员平台日报表
    fun startMemberPlatformDailyReport(startDate: LocalDate) {
        val endDate = startDate.plusDays(1)
        val now = LocalDateTime.now()
        val data = transferOrderService.report(startDate = startDate, endDate = endDate).groupBy { it.memberId }.map {

            var transferIn = BigDecimal.ZERO
            var transferOut = BigDecimal.ZERO
            var platform = Platform.Center

            it.value.forEach { vo ->
                when(vo.from) {
                    Platform.Center -> {
                        transferIn = vo.money
                        platform = vo.to
                    }
                    else -> {
                        transferOut = vo.money
                        platform = vo.from
                    }
                }
            }

            val vo = it.value.first()

            MemberPlatformDailyReport(id = -1, day = startDate, clientId = vo.clientId, memberId = vo.memberId, platform = platform,
                    createdTime = now, transferIn = transferIn, transferOut = transferOut)
        }

        memberPlatformDailyReportService.create(data)
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

            MemberDailyReport(id = -1, day = startDate, clientId = it.clientId, memberId = it.memberId,
                    transferIn = it.transferIn, transferOut = it.transferOut, depositMoney = deposit, withdrawMoney = withdraw,
                    createdTime = now)
        }
        memberDailyReportService.create(reports)
    }

    // 厅主平台日报表
    fun startClientPlatformReport(startDate: LocalDate) {
        val endDate = startDate.plusDays(1)
        val now = LocalDateTime.now()

        val data = transferOrderService.reportByClient(startDate = startDate, endDate = endDate).groupBy { it.clientId }.map {

            var transferIn = BigDecimal.ZERO
            var transferOut = BigDecimal.ZERO
            var platform = Platform.Center

            it.value.forEach { vo ->
                when(vo.from) {
                    Platform.Center -> {
                        transferOut = vo.money
                        platform = vo.from
                    }
                    else -> {
                        transferIn = vo.money
                        platform = vo.to
                    }
                }
            }

            val vo = it.value.first()

            ClientPlatformDailyReport(id = -1, day = startDate, clientId = vo.clientId, platform = platform,
                    createdTime = now, transferIn = transferIn, transferOut = transferOut)
        }

        clientPlatformDailyReportService.create(data)

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

            ClientDailyReport(id = -1, day = startDate, transferIn = it.transferIn, transferOut = it.transferOut,
                    depositMoney = depositReports[it.clientId]?.money?: BigDecimal.ZERO, depositCount = depositReports[it.clientId]?.count?: 0,
                    withdrawMoney = withdrawReports[it.clientId]?.money?: BigDecimal.ZERO, withdrawCount = withdrawReports[it.clientId]?.count?: 0,
                    createdTime = now, clientId = it.clientId, newMemberCount = 100) //TODO 暂时没时间写
        }

        clientDailyReportService.create(reports)
    }

}