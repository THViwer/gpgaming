//package com.onepiece.treasure.config
//
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver
//import org.springframework.mobile.device.DeviceResolverHandlerInterceptor
//import org.springframework.web.method.support.HandlerMethodArgumentResolver
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
//
//
//@Configuration
//class WebMvcConfig: WebMvcConfigurerAdapter() {
//
//    @Bean
//    fun deviceResolverHandlerInterceptor(): DeviceResolverHandlerInterceptor {
//        return DeviceResolverHandlerInterceptor()
//    }
//
//    @Bean
//    fun deviceHandlerMethodArgumentResolver(): DeviceHandlerMethodArgumentResolver {
//        return DeviceHandlerMethodArgumentResolver()
//    }
//
//
//    override fun addInterceptors(registry: InterceptorRegistry) {
//        registry.addInterceptor(deviceResolverHandlerInterceptor())
//    }
//
//    override fun addArgumentResolvers(
//            argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
//        argumentResolvers.add(deviceHandlerMethodArgumentResolver())
//    }
//
//
//}