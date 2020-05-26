package com.onepiece.gpgaming.su.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.servlet.config.annotation.CorsRegistry

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@Profile("dev")
open class GlobalDevCorsConfig {

    @Bean
    open fun corsConfigurer(): WebMvcConfigurer {

        return object : WebMvcConfigurer {
            
            override fun addCorsMappings(registry: CorsRegistry) {
                //添加映射路径
                registry.addMapping("/**") //放行哪些原始域
                        .allowedOrigins("*") //是否发送Cookie信息
                        .allowCredentials(true) //放行哪些原始域(请求方式)
                        .allowedMethods("GET", "POST", "PUT", "DELETE") //放行哪些原始域(头部信息)
                        .allowedHeaders("*") //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
                        .exposedHeaders("Header1", "Header2")
            }
        }

    }

}