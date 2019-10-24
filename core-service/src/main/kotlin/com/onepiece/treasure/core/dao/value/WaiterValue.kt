package com.onepiece.treasure.core.dao.value

import com.onepiece.treasure.core.model.enums.Status

data class WaiterCo(
        // 厅主Id
        val clientId: Int,

        // 登陆用户名
        val username: String,

        // 密码
        val password: String,

        // 名称 昵称
        val name: String

)

data class WaiterUo(

        val id: Int,

        // 密码
        val password: String,

        // 名称 昵称
        val name: String,

        // 状态
        val status: Status
)