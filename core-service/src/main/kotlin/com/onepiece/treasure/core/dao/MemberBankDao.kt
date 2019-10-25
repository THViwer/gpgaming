package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.MemberBankCo
import com.onepiece.treasure.beans.value.database.MemberBankUo
import com.onepiece.treasure.beans.model.MemberBank
import com.onepiece.treasure.beans.value.database.MemberBankQuery

interface MemberBankDao: BasicDao<MemberBank> {

    fun query(memberBankQuery: MemberBankQuery): List<MemberBank>

    fun create(memberBankCo: MemberBankCo): Boolean

    fun update(memberBankUo: MemberBankUo): Boolean

}