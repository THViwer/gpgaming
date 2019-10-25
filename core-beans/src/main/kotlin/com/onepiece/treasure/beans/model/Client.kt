package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

/**
 * 厅主表
 */
data class Client(

        // 用户Id
        val id: Int,

        // 品牌
        val brand: String,

        // 用户名
        val username: String,

        // 用户密码
        val password: String,

        // 创建时间
        val createdTime: LocalDateTime,

        // 登陆时间
        val loginTime: LocalDateTime,

        // 厅主状态
        val status: Status
)