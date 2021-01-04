package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.ClientPlatformDailyReport
import com.onepiece.gpgaming.beans.value.database.OtherPlatformReportValue
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberValue
import com.onepiece.gpgaming.core.dao.OtherPlatformReportDao
import com.onepiece.gpgaming.core.dao.TransferOrderDao
import com.onepiece.gpgaming.core.dao.TransferReportQuery
import com.onepiece.gpgaming.core.service.ClientPlatformDailyReportService
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.games.GameApi
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.Exception
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 其它平台的定时任务: Kiss918、Pussy888、Mega
 */
@Component
class OtherPlatformTask(
        private val platformBindService: PlatformBindService,
        private val otherPlatformReportDao: OtherPlatformReportDao,
        private val gameApi: GameApi,
        private val platformMemberService: PlatformMemberService,

        private val clientPlatformDailyReportService: ClientPlatformDailyReportService,
        private val transferOrderDao: TransferOrderDao
) {

    private val list = listOf(Platform.Kiss918, Platform.Pussy888, Platform.Mega)
    private val log = LoggerFactory.getLogger(OtherPlatformTask::class.java)

    @Scheduled(cron = "0 30 0 * * ?")
//    @Scheduled(cron = "0/10 * * * * ?")
    fun start() {
        val binds = platformBindService.all()
                .filter {
                    it.platform == Platform.Kiss918 || it.platform == Platform.Pussy888 || it.platform == Platform.Mega
                }.filter { it.clientId == 1 }

        val startDate = LocalDate.now().minusDays(1)

        binds.forEach { bind ->
            val list = gameApi.queryReport(clientId = bind.clientId, platform = bind.platform, startDate = startDate)
            if (list.isEmpty()) return@forEach

            val usernames = list.map { it.username }
            val memberQuery = PlatformMemberValue.PlatformMemberQuery(clientId = bind.clientId, platform = bind.platform, usernames = usernames)
            val members = platformMemberService.list(memberQuery)
                    .map { it.username to it }
                    .toMap()

            val data = list.mapNotNull { data ->
                members[data.username]?.let { member ->
                    OtherPlatformReportValue.PlatformReportCo(clientId = member.clientId, memberId = member.memberId, bet = data.bet,
                            win = data.win, platform = data.platform, originData = data.originData, day = startDate)
                }
            }
            if (data.isNotEmpty()) {
                otherPlatformReportDao.batch(data)


                try {
                    this.addOtherPlatformReport(startDate = startDate, data = data)
                } catch (e: Exception) {
                    log.info("添加Mega、Pussy、918平台的平台日报表错误")
                }
            }
        }
    }

    private fun addOtherPlatformReport(startDate: LocalDate, data: List<OtherPlatformReportValue.PlatformReportCo>) {
        val endDate = startDate.plusDays(1)

        // 转账信息
        val transferQuery = TransferReportQuery(startDate = startDate, endDate = endDate, clientId = null, memberId = null, from = null, to = null)
        val transferReports = transferOrderDao.clientPlatformReport(transferQuery)
                .map { "${it.clientId}:${it.from}:${it.to}" to it }
                .toMap()

        // 存活人数
        val activeCountMap = transferOrderDao.queryActiveCount(startDate = startDate, endDate = endDate)
                .map { "${it.clientId}:${it.platform}" to it.count }
                .toMap()

        val reports = data.map { opr ->
            val clientId = opr.clientId
            val platform = opr.platform

            val activeCount = activeCountMap["${opr.clientId}:${opr.platform}"] ?: 0

            val transferInVo = transferReports["${clientId}:${platform}:${Platform.Center}"]
            val transferOutVo = transferReports["${clientId}:${Platform.Center}:${platform}"]
            val transferIn = transferInVo?.money ?: BigDecimal.ZERO
            val transferOut = transferOutVo?.money ?: BigDecimal.ZERO
            val promotionAmount = (transferOutVo?.promotionAmount ?: BigDecimal.ZERO)
                    .plus(transferInVo?.promotionAmount ?: BigDecimal.ZERO)

            ClientPlatformDailyReport(day = opr.day.toString(), clientId = opr.clientId, platform = opr.platform, bet = opr.bet, payout = opr.win,
                    transferIn = transferIn, transferOut = transferOut, promotionAmount = promotionAmount, activeCount = activeCount, createdTime = LocalDateTime.now(),
                    status = Status.Normal)
        }
        clientPlatformDailyReportService.create(reports)
    }

}