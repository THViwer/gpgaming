package com.onepiece.gpgaming.player

import org.apache.catalina.connector.Connector
import org.slf4j.LoggerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextClosedEvent
import org.springframework.stereotype.Component
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


@Component
class GracefulShutdownTomcat: TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent?> {
    private val log = LoggerFactory.getLogger(GracefulShutdownTomcat::class.java)

    @Volatile
    private var connector: Connector? = null

    private val waitTime = 30L

    override fun customize(connector: Connector?) {
        this.connector = connector
    }

    override fun onApplicationEvent(contextClosedEvent: ContextClosedEvent) {
        connector?.let {
            it.pause()
            val executor: Executor = it.protocolHandler.executor
            if (executor is ThreadPoolExecutor) {
                try {
                    val threadPoolExecutor: ThreadPoolExecutor = executor
                    threadPoolExecutor.shutdown()
                    if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) {
                        log.warn("Tomcat thread pool did not shut down gracefully within $waitTime seconds. Proceeding with forceful shutdown")
                    }
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }
    }
}