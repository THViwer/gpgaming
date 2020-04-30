package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal

data class PlatformBindCo(

        // 厅主Id
        val clientId: Int,

        // 保证金
        val earnestBalance: BigDecimal,

        // token信息
        val tokenJson: String,

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

        // 是否热门
        val hot: Boolean?,

        // 是否新游戏
        val new: Boolean?,

        // 密码
        val password: String?,

        // token信息
        val tokenJson: String?,

        // 保证金
        val earnestBalance: BigDecimal?,

        // 状态
        val status: Status?

)