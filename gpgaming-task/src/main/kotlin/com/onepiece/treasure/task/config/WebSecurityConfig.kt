package com.onepiece.treasure.task.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.firewall.DefaultHttpFirewall
import org.springframework.security.web.firewall.HttpFirewall


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class WebSecurityConfig: WebSecurityConfigurerAdapter(){

    @Bean
    @Primary
    open fun allowUrlEncodedSlashHttpFirewall(): HttpFirewall {
//        val firewall = StrictHttpFirewall()
//        firewall.setAllowUrlEncodedSlash(true)
//        return firewall
        return DefaultHttpFirewall()
    }


    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                // 由于使用的是JWT，我们这里不需要csrf
                .csrf().disable()

                .exceptionHandling().and()

                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 允许对于网站静态资源的无授权访问
                .antMatchers(
                        "/**",
                        "/system/startup",
                        "/statistics/today",
                        "/statistics",
                        "/user/share",
                        "/version/last",
                        "/user"
                ).permitAll()
                .antMatchers(HttpMethod.POST, "/order").permitAll()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/auth/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()

        // 禁用缓存
        httpSecurity.headers().cacheControl()
    }

    override fun configure(web: WebSecurity?) {
        super.configure(web)
        web!!.httpFirewall(allowUrlEncodedSlashHttpFirewall())
    }


}