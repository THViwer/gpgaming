package com.onepiece.gpgaming.beans.value.internet.web

sealed class SeoValue {

    data class SeoUo(
            val clientId: Int,

            val keywords: String,

            val description: String
    )

    data class SeoVo(
            val keywords: String,

            val description: String
    )

}