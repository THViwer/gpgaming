package com.onepiece.gpgaming.beans.value.database

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

sealed class SalesmanValue {

    data class SalesmanCo(

            // bossId
            @JsonIgnore
            val bossId: Int,

            // 业主Id
            @JsonIgnore
            val clientId: Int,

            // 用户名
            val username: String,

            // 密码
            val password: String,

            // 自己的顾客佣金
            val ownCustomerScale: BigDecimal,

            // 系统顾客佣金
            val systemCustomerScale: BigDecimal,

            // 创建时间
            val createdTime: LocalDateTime
    )

    data class SalesmanUo(

            // id
            val id: Int,

            // 用户名
            val username: String,

            // 自己的顾客佣金
            val ownCustomerScale: BigDecimal,

            // 系统顾客佣金
            val systemCustomerScale: BigDecimal,

            // 状态
            val status: Status
    )

    data class SalesmanQuery(

            // bossId
            val bossId: Int,

            // clientId
            val clientId: Int,

            // 用户名
            val username: String? = null

    )

}