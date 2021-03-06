package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Role


class LoginHistoryValue {

    data class LoginHistoryCo(

            // bossId
            val bossId: Int,

            // 业主id
            val clientId: Int,

            // 用户Id
            val userId: Int,

            val username: String,

            // 角色
            val role: Role,

            // ip
            val ip: String,

            // 国家
            val country: String = "",

            // 渠道 pc、android、ios
            val deviceType: String

    )



}