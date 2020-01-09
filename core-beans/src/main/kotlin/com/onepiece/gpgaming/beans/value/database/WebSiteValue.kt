package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Status

data class WebSiteCo(

        // 厅主
        val clientId: Int,

        // 域名
        val domain: String
)

data class WebSiteUo(
        // id
        val id: Int,

        // 域名
        val domain: String,

        // 状态
        val status: Status = Status.Normal

)