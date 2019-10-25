package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.MemberBankCo
import com.onepiece.treasure.beans.value.database.MemberBankUo
import com.onepiece.treasure.beans.model.MemberBank

interface MemberBankDao: BasicDao<MemberBank> {

    fun create(memberBankCo: MemberBankCo): Boolean

    fun update(memberBankUo: MemberBankUo): Boolean

}