package com.onepiece.treasure.core.dao.value

import com.onepiece.treasure.core.model.enums.Status

data class ClientBankCo(

        // 厅主名称
        val clientId: Int,

        // 银行卡号
        val bankCardNumber: String,

        // 银行名称
        val cardName: String
)

data class ClientBankUo(

        // id
        val id: Int,

        // 银行卡号
        val bankCardNumber: String,

        // 银行名称
        val cardName: String,

        // 状态
        val status: Status

)