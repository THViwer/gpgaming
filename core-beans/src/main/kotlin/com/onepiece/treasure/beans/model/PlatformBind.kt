package com.onepiece.treasure.beans.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.token.ClientToken
import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.beans.model.token.Kiss918ClientToken
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
            return when (platform) {
                Platform.Kiss918 -> jacksonObjectMapper().readValue<Kiss918ClientToken>(tokenJson)
                else -> jacksonObjectMapper().readValue<DefaultClientToken>(tokenJson)
            }
        }

}