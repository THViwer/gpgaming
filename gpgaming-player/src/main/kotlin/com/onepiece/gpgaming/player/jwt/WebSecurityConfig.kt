package com.onepiece.gpgaming.player.jwt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.firewall.DefaultHttpFirewall
import org.springframework.security.web.firewall.HttpFirewall


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class WebSecurityConfig: WebSecurityConfigurerAdapter(){


    @Autowired
    lateinit var unauthorizedHandler: JwtAuthenticationEntryPoint

    @Autowired
    lateinit var userDetailsService: UserDetailsService

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    lateinit var tokenStore: TokenStore

    @Autowired
    @Throws(Exception::class)
    fun authenticationManager(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder())
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
//        return object: PasswordEncoder {
//            override fun encode(rawPassword: CharSequence): String {
//                return DigestUtils.md5DigestAsHex(rawPassword.toString().toByteArray())
//            }
//
//            override fun matches(rawPassword: CharSequence, encodedPassword: String): Boolean {
//                val encodePasswd = encode(rawPassword)
//                return encodePasswd == encodedPassword
//            }
//        }
    }

    @Bean
    @Primary
    open fun allowUrlEncodedSlashHttpFirewall(): HttpFirewall {
//        val firewall = StrictHttpFirewall()
//        firewall.setAllowUrlEncodedSlash(true)
//        return firewall
        return DefaultHttpFirewall()
    }

    @Bean
    @Throws(Exception::class)
    open fun authenticationTokenFilterBean(): JwtAuthenticationTokenFilter {
        return JwtAuthenticationTokenFilter(jwtTokenUtil = jwtTokenUtil, tokenStore = tokenStore, passwordEncoder = passwordEncoder())
    }

    @Throws(Exception::class)
    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                // 由于使用的是JWT，我们这里不需要csrf
                .csrf().disable()

                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // 基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 允许对于网站静态资源的无授权访问
                .antMatchers(
                        "/",
                        "/demo/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/v2/api-docs/**",

                        "/api",
                        "/api/**",
                        "/user",
                        "/user/login_from_admin",
                        "/user/check/**",
                        "/user/country",
                        "/agent",
                        "/agent/contactUs",
                        "/agent/i18n",

                        "/cash/bank",
                        "/cash/upload/proof",

                        "/open/**",
                        "/mega",
                        "/mega/",
                        "/cmd",
                        "/cmd/",
                        "/ebet",
                        "/ebet/",
                        "/ebet/",
                        "/ebet/check",
                        "/spadeGaming",
                        "/spadeGaming/",
                        "/gameplay",
                        "/gameplay/",
                        "/png/order",
                        "/png/order/",
                        "/api/auth",
                        "/api/auth/",

                        "/pay/**"
                ).permitAll()
                .antMatchers(HttpMethod.POST, "/order").permitAll()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/auth/**").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()

        // 添加JWT filter
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter::class.java)

        // 禁用缓存
        httpSecurity.headers().cacheControl()
    }

    override fun configure(web: WebSecurity?) {
        super.configure(web)
        web!!.httpFirewall(allowUrlEncodedSlashHttpFirewall())
    }


}