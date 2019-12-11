package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

/**
 * 客服人员
 */
data class Waiter (

        // id
        val id: Int,

        // 厅主Id
        val clientId: Int,

        // 登陆用户名
        val username: String,

        // 密码
        val password: String,

        // 维护厅主入款银行卡Id列表
        val clientBankData: String?,

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
            return clientBankData?.let { it.split(",").map { it.toInt() } }
        }


}