package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.value.database.AgentReportValue
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.core.service.AgentMonthReportService
import com.onepiece.gpgaming.core.service.MemberDailyReportService
import com.onepiece.gpgaming.core.service.WalletService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class RebateTask(
        private val memberDailyReportService: MemberDailyReportService,
        private val walletService: WalletService,
        private val agentMonthReportService: AgentMonthReportService
) {

    private val log = LoggerFactory.getLogger(RebateTask::class.java)


    // 返水任务
    @Scheduled(cron = "0 0 5 * * ?")
    fun start() {
        log.info("----------------------")
        log.info("----------------------")
        log.info("----------------------")
        log.info("----------------------")
        log.info("${LocalDate.now()}开始执行返水任务")

        var index = 0
        var next: Boolean = true
        do {
            val list = memberDailyReportService.queryRebate(current = index, size = 1000)

            log.info("${LocalDate.now()}开始执行返水任务. 从${index}到${index+1000}条数据")

            val ids = list.filter { it.rebateAmount.toDouble() > 0 }.mapNotNull {

                try {
                    val walletUo = WalletUo(clientId = it.clientId, waiterId = null, memberId = it.memberId, money = it.rebateAmount, eventId = "${it.id}",
                            event = WalletEvent.Rebate, remarks = "system rebate")
                    walletService.update(walletUo)

                    it.id
                } catch (e: Exception) {
                    log.error("", e)
                    null
                }
            }

            if (ids.isNotEmpty())
                memberDailyReportService.updateRebate(ids)

            index += 1000
            next = list.isNotEmpty()
        } while (next)

        log.info("${LocalDate.now()}开始执行返水任务结束")
        log.info("----------------------")
        log.info("----------------------")
        log.info("----------------------")
        log.info("----------------------")

    }

    @Scheduled(cron = "0 0 6 * * ?")
    fun startAgentCommission() {

        val data = agentMonthReportService.commissions()
        val ids = data.mapNotNull { report ->
            try {
                val money = report.agentCommission.plus(report.memberCommission)
                val walletUo = WalletUo(clientId = report.clientId, waiterId = null, memberId = report.agentId, money = money, eventId = "${report.id}",
                        event = WalletEvent.Commission, remarks = "agent commission")
                walletService.update(walletUo)
                report.id
            } catch (e: Exception) {
                null
            }
        }

        agentMonthReportService.executionCommission(ids)
    }

}