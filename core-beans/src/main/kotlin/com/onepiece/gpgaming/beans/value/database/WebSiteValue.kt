package com.onepiece.gpgaming.beans.value.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.enums.Status

data class WebSiteCo(

        // bossId
        val bossId: Int,

        // 厅主
        val clientId: Int,

        // 国家
        @JsonIgnore
        val country: Country = Country.Default,

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