package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

/**
 * 厅主表
 */
data class Client(

        // 用户Id
        val id: Int,

        // bossId
        val bossId: Int,

        // 主要业主
        val main: Boolean,

        // 国家
        val country: Country,

        // logo
        val logo: String,

        // tab logo
        val shortcutLogo: String,

        // 昵称
        val name: String,

        // 用户名
        val username: String,

        // 用户密码
        val password: String,

        // 进程Id
//        val processId: String,

        // 厅主状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime,

        // 当前登陆Ip
        val loginIp: String?,

        // 登陆时间
        val loginTime: LocalDateTime?,

        // 白敏感
        val whitelists: List<String>

)