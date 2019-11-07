package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status
import java.math.BigDecimal

data class PlatformBindCo(

        // 厅主Id
        val clientId: Int,

        // 保证金
        val earnestBalance: BigDecimal,

        // 用户名
        val username: String,

        // 密码
        val password: String,

        // 平台
        val platform: Platform

)

data class PlatformBindUo(

        // id
        val id: Int,

        // 用户名
        val username: String?,

        // 密码
        val password: String?,

        // 保证金
        val earnestBalance: BigDecimal?,

        // 状态
        val status: Status?

)