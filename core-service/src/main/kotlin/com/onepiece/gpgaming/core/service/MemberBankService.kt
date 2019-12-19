package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.model.MemberBank
import com.onepiece.gpgaming.beans.value.database.MemberBankCo
import com.onepiece.gpgaming.beans.value.database.MemberBankUo

interface MemberBankService {

    fun get(bankId: Int): MemberBank

    fun query(memberId: Int): List<MemberBank>

    fun create(memberBankCo: MemberBankCo): Int

    fun update(memberBankUo: MemberBankUo)

}