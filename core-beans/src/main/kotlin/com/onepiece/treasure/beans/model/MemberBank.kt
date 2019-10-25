package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Banks
import com.onepiece.treasure.beans.enums.Status
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
        val bank: Banks,

        // 会员姓名
        val name: String,

        // 银行卡号
        val bankCardNumber: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)