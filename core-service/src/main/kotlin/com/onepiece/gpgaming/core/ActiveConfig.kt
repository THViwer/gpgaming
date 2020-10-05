package com.onepiece.gpgaming.core

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


@Configuration
@Component
class ActiveConfig {

    @Value("\${spring.profiles.active}")
    lateinit var profile: String

    fun isProd(): Boolean {
        return when (this.profile) {
            "prod", "prods2" -> true
            else -> false
        }
    }
}
