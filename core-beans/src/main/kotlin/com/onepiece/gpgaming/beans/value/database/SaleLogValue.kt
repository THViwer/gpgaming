package com.onepiece.gpgaming.beans.value.database

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime

sealed class SaleLogValue {

    data class SaleLogCo(

            @JsonIgnore
            val bossId: Int,

            @JsonIgnore
            val clientId: Int,

            @JsonIgnore
            val saleId: Int? = null,

            val memberId: Int,

            val remark: String,

            // 下一次电话时间
            val nextCallTime: LocalDateTime?
    )

    data class SaleLogQuery(
            val bossId: Int,

            val clientId: Int,

            val saleId: Int?,

            val memberId: Int?
    )

}