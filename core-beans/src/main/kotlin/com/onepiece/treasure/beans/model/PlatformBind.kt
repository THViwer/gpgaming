package com.onepiece.treasure.beans.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.token.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 厅主开通平台表
 */
data class PlatformBind(

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform,

        // 用户名
        val username: String,

        // 密码
        val password: String,

        // 保证金
        val earnestBalance: BigDecimal,

        // tokenJson
        val tokenJson: String,

        // 进程Id
        val processId: String,

        // 状态
        val status: Status,

        // 开通时间
        val createdTime: LocalDateTime

) {

    // token信息
    val clientToken: ClientToken
        get() {

            val objectMapper = jacksonObjectMapper()

            val clz =  when (platform) {
                Platform.Kiss918 -> Kiss918ClientToken::class.java
                Platform.Mega -> MegaClientToken::class.java
                Platform.Pussy888 -> Pussy888ClientToken::class.java
                Platform.SexyGaming -> SexyClientToken::class.java
                Platform.Evolution -> EvolutionClientToken::class.java
                Platform.AllBet -> AllBetClientToken::class.java
                Platform.GGFishing -> GGFishingClientToken::class.java
                Platform.DreamGaming -> DreamGamingClientToken::class.java
                Platform.Lbc -> LbcClientToken::class.java
                else -> DefaultClientToken::class.java
            }

            return objectMapper.readValue(tokenJson, clz)
        }

}