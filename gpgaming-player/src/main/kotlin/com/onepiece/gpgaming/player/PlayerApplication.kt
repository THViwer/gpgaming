package com.onepiece.gpgaming.player

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@SpringBootApplication
@EnableAsync
@ComponentScan("com.onepiece.gpgaming")
open class PlayerApplication {

    @Autowired
    private lateinit var gracefulShutdownTomcat: GracefulShutdownTomcat

    @Bean
    open fun servletContainer(): ServletWebServerFactory? {
        val tomcat = TomcatServletWebServerFactory()
        tomcat.addConnectorCustomizers(gracefulShutdownTomcat)
        return tomcat
    }
}

fun main() {
    runApplication<PlayerApplication>()

    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
}