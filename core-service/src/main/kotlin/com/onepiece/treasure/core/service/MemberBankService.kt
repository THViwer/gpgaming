package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.MemberBank
import com.onepiece.treasure.beans.value.database.MemberBankCo
import com.onepiece.treasure.beans.value.database.MemberBankUo

interface MemberBankService {

    fun get(bankId: Int): MemberBank

    fun query(memberId: Int): List<MemberBank>

    fun create(memberBankCo: MemberBankCo): Int

    fun update(memberBankUo: MemberBankUo)

}