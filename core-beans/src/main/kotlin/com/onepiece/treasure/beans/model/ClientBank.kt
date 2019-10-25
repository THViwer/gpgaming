package com.onepiece.treasure.beans.model

import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

/**
 * 厅主银行卡信息
 */
data class ClientBank(

        // id
        val id: Int,

        // 厅主名称
        val clientId: Int,

        // 银行卡号
        val bankCardNumber: String,

        // 银行名称
        val cardName: String,

        // 状态
        val status: Status,

        // 创建时间
        val createdTime: LocalDateTime
)