package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.WalletEvent
import com.onepiece.gpgaming.beans.value.database.WalletUo
import com.onepiece.gpgaming.core.service.MemberDailyReportService
import com.onepiece.gpgaming.core.service.WalletService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class BackwaterTask(
        private val memberDailyReportService: MemberDailyReportService,
        private val walletService: WalletService
) {

    private val log = LoggerFactory.getLogger(BackwaterTask::class.java)


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
            val list = memberDailyReportService.queryBackwater(current = index, size = 1000)

            log.info("${LocalDate.now()}开始执行返水任务. 从${index}到${index+1000}条数据")

            val ids = list.filter { it.backwaterMoney.toDouble() > 0 }.mapNotNull {

                try {
                    val walletUo = WalletUo(clientId = it.clientId, waiterId = null, memberId = it.memberId, money = it.backwaterMoney, eventId = "${it.id}",
                            event = WalletEvent.Backwater, remarks = "system backwater")
                    walletService.update(walletUo)

                    it.id
                } catch (e: Exception) {
                    log.error("", e)
                    null
                }
            }

            if (ids.isNotEmpty())
                memberDailyReportService.updateBackwater(ids)

            index += 1000
            next = list.isNotEmpty()
        } while (next)

        log.info("${LocalDate.now()}开始执行返水任务结束")
        log.info("----------------------")
        log.info("----------------------")
        log.info("----------------------")
        log.info("----------------------")

    }


}