package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

data class WaiterCo(
        // 厅主Id
        val clientId: Int,

        // 登陆用户名
        val username: String,

        // 密码
        val password: String,

        // 入款银行卡Id
        val clientBankData: String?,

        // 名称 昵称
        val name: String

)

data class WaiterUo(

        val id: Int,

        val oldPassword: String? = null,

        // 密码
        val password: String? = null,

        // 入款银行卡Id
        val clientBankData: String?,

        // 名称 昵称
        val name: String? = null,

        // 状态
        val status: Status? = null,

        // 登陆Ip
        val loginIp: String? = null,

        // 登陆时间
        val loginTime: LocalDateTime? = null
)