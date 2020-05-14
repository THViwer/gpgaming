package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal


sealed class LevelValue {

    data class LevelUo(

            // id
            val id: Int,

            // 名称
            val name: String?,

            // 状态
            val status: Status?,

            // 体育返水
            val sportRebate: BigDecimal?,

            // 真人返水
            val liveRebate: BigDecimal?,

            // 老虎机返水
            val slotRebate: BigDecimal?,

            // 捕鱼返水
            val fishRebate: BigDecimal?
    )

    data class LevelCo(

            // 厅主Id
            val clientId: Int,

            // 名称
            val name: String,

            // 体育返水
            val sportRebate: BigDecimal,

            // 真人返水
            val liveRebate: BigDecimal,

            // 老虎机返水
            val slotRebate: BigDecimal,

            // 捕鱼返水
            val fishRebate: BigDecimal

    )
}
