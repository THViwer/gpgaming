package com.onepiece.treasure.core.dao.value

import com.onepiece.treasure.core.model.enums.Status

data class LevelUo(
        val id: Int,

        val name: String?,

        val status: Status?
)

data class LevelCo(

        // 厅主Id
        val clientId: Int,

        // 名称
        val name: String
)