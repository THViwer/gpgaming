package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

data class ClientCo(

        // bossId
        val bossId: Int,

        // 国家
        val country: Country,

        // 昵称
        val name: String,

        // 平台logo
        val logo: String,

        // tab logo
        val shortcutLogo: String,

        // 用户名
        val username: String,

        // 用户密码
        val password: String,

        // 白名单
        val whitelists: List<String>

)

data class ClientUo(

        // 用户Id
        val id: Int,

        // 昵称
        val name: String?,

        // 平台logo
        val logo: String?,

        // tab logo
        val shortcutLogo: String?,

        // 旧密码
        val oldPassword: String? = null,

        // 用户密码
        val password: String? = null,

        // 厅主状态
        val status: Status? = null,

        // 请求Ip
        val ip: String? = null,

        // 登陆时间
        val loginTime: LocalDateTime? = null,

        // ip 白名单
        val whitelists: List<String>? = null
)