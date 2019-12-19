package com.onepiece.gpgaming.beans.model

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.enums.Status
import java.time.LocalDateTime

/**
 * 厅主银行卡信息
 */
data class MemberBank(

        // id
        val id: Int,

        // 厅主名称
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 银行
        val bank: Bank,

        // 银行卡号
        val bankCardNumber: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)