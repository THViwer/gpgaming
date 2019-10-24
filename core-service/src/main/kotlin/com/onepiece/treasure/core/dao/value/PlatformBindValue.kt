package com.onepiece.treasure.core.dao.value

import com.onepiece.treasure.core.model.enums.Platform
import com.onepiece.treasure.core.model.enums.Status

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