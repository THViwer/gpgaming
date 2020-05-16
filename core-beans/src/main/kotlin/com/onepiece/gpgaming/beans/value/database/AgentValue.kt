package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

sealed class AgentValue {

    data class AgentCo(

            // 用户Id
            val bossId: Int,

            // 用户名
            val username: String,

            // 密码
            val password: String,

            // 状态
            val status: Status,

            // 推广code
            val code: String,

            // 登陆时间
            val loginTime: LocalDateTime
    )

    data class AgentUo(

            // Id
            val id: Int,

            // 密码
            val password: String?,

            // 状态
            val status: Status?,

            // 推广code
            val code: String?,

            // 登陆时间
            val loginTime: LocalDateTime?
    )

}