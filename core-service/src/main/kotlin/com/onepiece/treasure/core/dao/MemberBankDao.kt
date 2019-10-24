package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.MemberBankCo
import com.onepiece.treasure.core.dao.value.MemberBankUo
import com.onepiece.treasure.core.model.MemberBank

interface MemberBankDao: BasicDao<MemberBank> {

    fun create(memberBankCo: MemberBankCo): Boolean

    fun update(memberBankUo: MemberBankUo): Boolean

}