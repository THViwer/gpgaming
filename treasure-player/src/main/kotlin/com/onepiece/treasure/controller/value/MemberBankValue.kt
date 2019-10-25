package com.onepiece.treasure.controller.value

import com.onepiece.treasure.beans.enums.Banks
import com.onepiece.treasure.beans.enums.Status
import java.time.LocalDateTime

object MemberBankValueFactory {

    fun generatorMemberBanks(): List<MemberBankVo> {
        val now = LocalDateTime.now()
        val b1 = MemberBankVo(id = 1, clientId = 1, memberId = 1, bank = Banks.ABC, name = "张三", bankCardNumber = "622222", status = Status.Normal, createdTime = now)
        val b2 = b1.copy(id = 2, bank = Banks.ICBC, bankCardNumber = "6333333")
        val b3 = b1.copy(id = 3, bankCardNumber = "644444")

        return listOf(b1, b2, b3)
    }

}

data class MemberBankVo(

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


data class MemberBankCo(

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