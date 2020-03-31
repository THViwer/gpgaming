package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal

data class LevelUo(

        // id
        val id: Int,

        // 名称
        val name: String?,

        // 状态
        val status: Status?,

        // 返水比例
        val backwater: BigDecimal?
)

data class LevelCo(

        // 厅主Id
        val clientId: Int,

        // 名称
        val name: String,

        // 返水比例
        val backwater: BigDecimal
)