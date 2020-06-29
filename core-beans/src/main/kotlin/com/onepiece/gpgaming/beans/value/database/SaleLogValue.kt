package com.onepiece.gpgaming.beans.value.database

import com.fasterxml.jackson.annotation.JsonIgnore

sealed class SaleLogValue {

    data class SaleLogCo(

            @JsonIgnore
            val bossId: Int,

            @JsonIgnore
            val clientId: Int,

            @JsonIgnore
            val saleId: Int,

            val memberId: Int,

            val remark: String
    )

    data class SaleLogQuery(
            val bossId: Int,

            val clientId: Int,

            val saleId: Int?,

            val memberId: Int?
    )

}