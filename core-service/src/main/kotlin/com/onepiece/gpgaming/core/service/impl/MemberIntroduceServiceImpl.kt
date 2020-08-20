package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.MemberIntroduce
import com.onepiece.gpgaming.beans.value.database.MemberIntroduceValue
import com.onepiece.gpgaming.core.dao.MemberIntroduceDao
import com.onepiece.gpgaming.core.service.MemberIntroduceService
import org.springframework.stereotype.Service

@Service
class MemberIntroduceServiceImpl(
        private val memberIntroduceDao: MemberIntroduceDao
) : MemberIntroduceService {


    override fun get(memberId: Int): MemberIntroduce? {
        return memberIntroduceDao.getByMemberId(memberId = memberId)
    }

    override fun create(co: MemberIntroduceValue.MemberIntroduceCo) {
        val flag = memberIntroduceDao.create(co = co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(uo: MemberIntroduceValue.MemberIntroduceUo) {
        val flag = memberIntroduceDao.update(uo = uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun list(query: MemberIntroduceValue.MemberIntroduceQuery): List<MemberIntroduce> {
        return memberIntroduceDao.list(query = query)
    }

}