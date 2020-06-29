package com.onepiece.gpgaming.beans.value.database

sealed class SaleLogValue {

    data class SaleLogCo(

            val bossId: Int,

            val clientId: Int,

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