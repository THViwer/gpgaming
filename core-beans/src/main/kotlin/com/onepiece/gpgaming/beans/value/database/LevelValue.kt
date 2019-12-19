package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Status

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