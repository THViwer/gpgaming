package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

data class MemberQuery(
        val clientId: Int,

        val username: String?,

        val status: Status?,

        val levelId: Int?,

        val startTime: LocalDateTime?,

        val endTime: LocalDateTime?
)

data class MemberCo(

        // 厅主Id
        val clientId: Int,

        // 用户名
        val username: String,

        // 密码
        val password: String,

        // 等级Id
        val levelId: Int
)

data class MemberUo(

        val id: Int,

        // 密码
        val password: String?,

        // 等级Id
        val levelId: Int?,

        // 状态
        val status: Status?
)


