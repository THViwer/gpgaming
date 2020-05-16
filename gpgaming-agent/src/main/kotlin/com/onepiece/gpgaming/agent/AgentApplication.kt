package com.onepiece.gpgaming.agent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import java.util.*

@SpringBootApplication
@ComponentScan("com.onepiece.gpgaming")
open class AgentApplication {

/*    @Autowired
    private lateinit var gracefulShutdownTomcat: GracefulShutdownTomcat

    @Bean
    open fun servletContainer(): ServletWebServerFactory? {
        val tomcat = TomcatServletWebServerFactory()
        tomcat.addConnectorCustomizers(gracefulShutdownTomcat)
        return tomcat
    }*/

}


fun main() {
    runApplication<AgentApplication>()

    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
}