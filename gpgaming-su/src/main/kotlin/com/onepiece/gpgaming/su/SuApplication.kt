package com.onepiece.gpgaming.su

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import java.util.*

@SpringBootApplication
@ComponentScan("com.onepiece.gpgaming")
open class SuApplication {

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
    runApplication<SuApplication>()

    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
}