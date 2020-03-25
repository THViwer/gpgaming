package com.onepiece.gpgaming.player

import org.apache.tomcat.util.http.LegacyCookieProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync
import java.util.*
import org.springframework.boot.web.server.WebServerFactoryCustomizer as WebServerFactoryCustomizer1


@SpringBootApplication
@EnableAsync
@ComponentScan("com.onepiece.gpgaming")
open class PlayerApplication: SpringBootServletInitializer() {

    @Autowired
    private lateinit var gracefulShutdownTomcat: GracefulShutdownTomcat

    @Bean
    open fun servletContainer(): ServletWebServerFactory? {
        val tomcat = TomcatServletWebServerFactory()
        tomcat.addConnectorCustomizers(gracefulShutdownTomcat)
        return tomcat
    }

    @Bean
    open fun cookieProcessorCustomizer(): WebServerFactoryCustomizer1<TomcatServletWebServerFactory> {
        return WebServerFactoryCustomizer1{ factory ->
            factory.addContextCustomizers(TomcatContextCustomizer { content ->
                content.cookieProcessor = LegacyCookieProcessor()
            })
        }
    }

}

fun main() {
    runApplication<PlayerApplication>()

    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
}