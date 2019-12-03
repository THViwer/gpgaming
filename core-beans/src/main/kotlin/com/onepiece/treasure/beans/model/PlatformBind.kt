package com.onepiece.treasure.beans.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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

    companion object {

        fun getClientTokenClass(platform: Platform): Class<out ClientToken> {
            return when (platform) {
                Platform.Kiss918 -> Kiss918ClientToken::class.java
                Platform.Mega -> MegaClientToken::class.java
                Platform.Pussy888 -> Pussy888ClientToken::class.java
                Platform.SexyGaming -> SexyGamingClientToken::class.java
                Platform.Evolution -> EvolutionClientToken::class.java
                Platform.AllBet -> AllBetClientToken::class.java
                Platform.GGFishing -> GGFishingClientToken::class.java
                Platform.DreamGaming -> DreamGamingClientToken::class.java
                Platform.Lbc -> LbcClientToken::class.java
                Platform.Pragmatic -> PragmaticClientToken::class.java
                Platform.SpadeGaming -> SpadeGamingClientToken::class.java
                Platform.TTG -> TTGClientToken::class.java
                Platform.CMD -> CMDClientToken::class.java
                Platform.MicroGaming -> MicroGamingClientToken::class.java
                Platform.GoldDeluxe -> GoldDeluxeClientToken::class.java
                Platform.Bcs -> BcsClientToken::class.java
                else -> DefaultClientToken::class.java
            }
        }
    }

    // token信息
    val clientToken: ClientToken
        get() {
            val objectMapper = jacksonObjectMapper()
            val clz =  getClientTokenClass(platform)
            return objectMapper.readValue(tokenJson, clz)
        }

}