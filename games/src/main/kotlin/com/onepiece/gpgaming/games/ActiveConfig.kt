package com.onepiece.gpgaming.games

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration


@Configuration
class ActiveConfig {

    @Value("\${spring.profiles.active}")
    lateinit var profile: String
}
