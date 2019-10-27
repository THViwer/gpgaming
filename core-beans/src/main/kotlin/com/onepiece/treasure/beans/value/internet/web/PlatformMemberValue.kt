package com.onepiece.treasure.beans.value.internet.web

import com.onepiece.treasure.beans.enums.Platform

data class PlatformMemberVo(

        // id
        val id: Int,

        // 用户Id
        val memberId: Int,

        // 平台
        val platform: Platform,

        // 平台账号
        val platformUsername: String,

        // 平台密码
        val platformPassword: String
)