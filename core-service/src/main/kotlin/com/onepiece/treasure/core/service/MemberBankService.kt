package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.model.MemberBank
import com.onepiece.treasure.beans.value.database.MemberBankCo
import com.onepiece.treasure.beans.value.database.MemberBankUo

interface MemberBankService {

    fun query(memberId: Int): List<MemberBank>

    fun create(memberBankCo: MemberBankCo)

    fun update(memberBankUo: MemberBankUo)

}