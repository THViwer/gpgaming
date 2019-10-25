package com.onepiece.treasure.beans.value.database

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.enums.Status

data class PlatformBindCo(

        // 厅主Id
        val clientId: Int,

        // 平台
        val platform: Platform

)

data class PlatformBindUo(

        val id: Int,

        // 状态
        val status: Status

)