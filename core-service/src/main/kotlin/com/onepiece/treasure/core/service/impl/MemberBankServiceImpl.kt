package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.MemberBank
import com.onepiece.treasure.beans.value.database.MemberBankCo
import com.onepiece.treasure.beans.value.database.MemberBankQuery
import com.onepiece.treasure.beans.value.database.MemberBankUo
import com.onepiece.treasure.core.dao.MemberBankDao
import com.onepiece.treasure.core.service.MemberBankService
import org.springframework.stereotype.Service

@Service
class MemberBankServiceImpl(
        private val memberBankDao: MemberBankDao
): MemberBankService {

    override fun query(memberBankQuery: MemberBankQuery): List<MemberBank> {
        return memberBankDao.query(memberBankQuery)
    }

    override fun create(memberBankCo: MemberBankCo) {
        val state = memberBankDao.create(memberBankCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(memberBankUo: MemberBankUo) {
        val state = memberBankDao.update(memberBankUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}