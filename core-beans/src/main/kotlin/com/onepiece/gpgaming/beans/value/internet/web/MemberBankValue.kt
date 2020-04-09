package com.onepiece.gpgaming.beans.value.internet.web

import com.onepiece.gpgaming.beans.enums.Bank

sealed class MemberBankValue {

    data class MemberBankUo(

            val id: Int,

            val memberId: Int,

            val bank: Bank?,

            val bankCardNo: String?

    )

}