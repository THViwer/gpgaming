package com.onepiece.treasure.core.dao.value

import com.onepiece.treasure.core.model.enums.Status
import java.time.LocalDateTime

data class ClientCo(

        // 品牌
        val brand: String,

        // 用户名
        val username: String,

        // 用户密码
        val password: String,

        // 登陆时间
        val loginTime: LocalDateTime
)

data class ClientUo(

        // 用户Id
        val id: Int,

        // 用户密码
        val password: String,

        // 厅主状态
        val status: Status

)