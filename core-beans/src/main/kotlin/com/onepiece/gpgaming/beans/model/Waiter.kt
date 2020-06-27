package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 客服人员
 */
data class Waiter (

        // id
        val id: Int,

        // bossId
        val bossId: Int,

        // 厅主Id
        val clientId: Int,

        // 角色
        val role: Role,

        // 登陆用户名
        val username: String,

        // 密码
        val password: String,

        // 维护厅主入款银行卡Id列表
        val clientBankData: String?,

        // 自己的顾客佣金
        val ownCustomerScale: BigDecimal,

        // 系统顾客佣金
        val systemCustomerScale: BigDecimal,

        // 名称 昵称
        val name: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime,

        // 登陆ip
        val loginIp: String?,

        // 登陆时间
        val loginTime: LocalDateTime?
) {

    val clientBanks: List<Int>?
        get() {
            return clientBankData?.let {
                it.split(",")
                        .let {
                            if (it.isEmpty() || (it.size == 1 && it.first().isEmpty())) emptyList() else it.map { x -> x.toInt() }
                        }
            }
        }
}