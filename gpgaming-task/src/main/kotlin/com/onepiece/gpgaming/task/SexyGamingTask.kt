package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.token.SexyGamingClientToken
import com.onepiece.gpgaming.core.service.PlatformBindService
import com.onepiece.gpgaming.games.live.SexyGamingService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class SexyGamingTask(
        private val platformBindService: PlatformBindService,
        private val sexyGamingService: SexyGamingService
) {

    private val log = LoggerFactory.getLogger(SexyGamingTask::class.java)

//    @Scheduled(cron="0/10 * *  * * ? ")
    fun reconciliation() {

        val binds = platformBindService.find(Platform.SexyGaming)
        if (binds.isEmpty()) return

        binds.forEach { bind ->
            val clientToken = bind.clientToken as SexyGamingClientToken
            val data = sexyGamingService.getSummaryByTxTimeHour(clientToken = clientToken, startDate = LocalDate.now().minusDays(1))

            log.info("clientId: ${bind.clientId}, 数据：$data")
        }
    }

}