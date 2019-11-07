package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status

data class PlatformBindCo(

        // 厅主Id
        val clientId: Int,

        val username: String?,

        val password: String?,
        // 平台
        val platform: Platform

)

data class PlatformBindUo(

        val id: Int,

        val username: String?,

        val password: String?,

        // 状态
        val status: Status

)