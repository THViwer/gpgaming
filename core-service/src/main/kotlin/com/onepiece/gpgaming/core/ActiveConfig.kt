package com.onepiece.gpgaming.core

import com.onepiece.gpgaming.core.utils.ActiveUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Configuration
@Component
class ActiveConfig {

    @Value("\${spring.profiles.active}")
    lateinit var profile: String

    @PostConstruct
    fun init() {
        ActiveUtil.active = profile
        ActiveUtil.isProd = true
    }

    fun isProd(): Boolean {
        return when (this.profile) {
            "prod", "prods2" -> true
            else -> false
        }
    }

}
