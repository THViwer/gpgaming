package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.MemberBank
import com.onepiece.gpgaming.beans.value.database.MemberBankCo
import com.onepiece.gpgaming.beans.value.database.MemberBankUo
import com.onepiece.gpgaming.core.dao.MemberBankDao
import com.onepiece.gpgaming.core.service.MemberBankService
import org.springframework.stereotype.Service
import java.lang.Exception

@Service
class MemberBankServiceImpl(
        private val memberBankDao: MemberBankDao
): MemberBankService {

    override fun query(memberId: Int): List<MemberBank> {
        return memberBankDao.query(memberId)
    }

    override fun create(memberBankCo: MemberBankCo): Int {
        check(memberBankCo.bankCardNumber.length >= 8) { OnePieceExceptionCode.BANK_CARD_ERROR }

        val id = memberBankDao.create(memberBankCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }
        return id
    }

    override fun exist(clientId: Int, bankNo: String): MemberBank? {
        return memberBankDao.get(clientId, bankNo)
    }

    override fun update(memberBankUo: MemberBankUo) {
        val state = memberBankDao.update(memberBankUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun get(bankId: Int): MemberBank {
        return memberBankDao.get(bankId)
    }
}