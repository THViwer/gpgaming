package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

data class MemberQuery(
        val clientId: Int?,

        val username: String?,

        val status: Status?,

        val levelId: Int?,

        val ids: List<Int>? = null,

        val startTime: LocalDateTime?,

        val endTime: LocalDateTime?
)

data class MemberCo(

        // 厅主Id
        val clientId: Int,

        // 用户名
        val username: String,

        // 姓名
        val name: String,

        // 手机号
        val phone: String,

        // 密码
        val password: String,

        // 安全密码
        val safetyPassword: String,

        // 等级Id
        val levelId: Int
)

data class MemberUo(

        val id: Int,

        // 姓名
        val name: String? = null,

        // 手机号
        val phone: String? = null,

        // 旧密码
        val oldPassword: String? = null,

        // 密码
        val password: String? = null,

        // 旧安全密码
        val oldSafetyPassword: String? = null,

        // 安全密码
        val safetyPassword: String? = null,

        // 等级Id
        val levelId: Int? = null,

        // 状态
        val status: Status? = null,

        // 登陆Ip
        val loginIp: String? = null,

        // 登陆时间
        val loginTime: LocalDateTime? = null
)


