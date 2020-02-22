package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.HotGameType
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status

sealed class HotGameValue {

    data class HotGameCo(

            // 厅主Id
            var clientId: Int = 1,

            // 游戏类型
            val type: HotGameType,

            // 游戏Id
            val gameId: String,

            // 平台
            val platform: Platform
    )

    data class HotGameUo(

            // Id
            val id: Int,

            // 游戏Id
            val gameId: String?,

            // 状态
            val status: Status?
    )

}