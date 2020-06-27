package com.onepiece.gpgaming.beans.value.database

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime


sealed class WaiterValue {


    data class WaiterCo(

            val bossId: Int,

            // 厅主Id
            val clientId: Int,

            // 角色
            val role: Role,

            // 登陆用户名
            val username: String,

            // 密码
            val password: String,

            // 入款银行卡Id
            val clientBankData: String?,

            // 名称 昵称
            val name: String,

            // 自己顾客的佣金
            val ownCustomerScale: BigDecimal,

            // 系统顾客的佣金
            val systemCustomerScale: BigDecimal
    )

    data class WaiterUo(

            val id: Int,

            val oldPassword: String? = null,

            // 密码
            val password: String? = null,

            // 入款银行卡Id
            val clientBankData: String?,

            // 名称 昵称
            val name: String? = null,

            // 状态
            val status: Status? = null,

            // 登陆Ip
            val loginIp: String? = null,

            // 登陆时间
            val loginTime: LocalDateTime? = null,

            // 自己顾客的佣金
            val ownCustomerScale: BigDecimal? = null,

            // 系统顾客的佣金
            val systemCustomerScale: BigDecimal? = null
    )


}
