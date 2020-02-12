package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.MemberBank
import com.onepiece.gpgaming.beans.value.database.MemberBankCo
import com.onepiece.gpgaming.beans.value.database.MemberBankUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface MemberBankDao: BasicDao<MemberBank> {

    fun query(memberId: Int): List<MemberBank>

    fun get(clientId: Int, bankNo: String): MemberBank?

    fun create(memberBankCo: MemberBankCo): Int

    fun update(memberBankUo: MemberBankUo): Boolean

}