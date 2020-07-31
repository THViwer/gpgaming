package com.onepiece.gpgaming.task

import com.onepiece.gpgaming.core.service.PayOrderService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ThirdPayTask {

    @Autowired
    lateinit var payOrderService: PayOrderService

    private val log = LoggerFactory.getLogger(ThirdPayTask::class.java)

    @Scheduled(cron="0 0/10 *  * * ? ")
//    @Scheduled(cron="0/20 * *  * * ? ")
    fun start() {
        val now = LocalDateTime.now()

        log.info("关闭订单任务启动，关闭订单的时间：${now.minusDays(1)}")
        payOrderService.close(now.minusDays(1))
    }

}