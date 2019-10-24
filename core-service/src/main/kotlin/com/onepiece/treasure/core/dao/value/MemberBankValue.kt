package com.onepiece.treasure.core.dao.value

import com.onepiece.treasure.core.model.enums.Banks
import com.onepiece.treasure.core.model.enums.Status

data class MemberBankCo(

        // 厅主名称
        val clientId: Int,

        // 会员Id
        val memberId: Int,

        // 银行
        val bank: Banks,

        // 会员姓名
        val name: String,

        // 银行卡号
        val bankCardNumber: String

)

data class MemberBankUo(
        // id
        val id: Int,

        // 银行
        val bank: Banks,

        // 会员姓名
        val name: String,

        // 银行卡号
        val bankCardNumber: String,

        // 状态
        val status: Status

)