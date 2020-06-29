package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.MemberInfo
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.core.dao.MemberInfoDao
import com.onepiece.gpgaming.core.service.MemberInfoService
import org.springframework.stereotype.Service

@Service
class MemberInfoServiceImpl(
        private val memberInfoDao: MemberInfoDao
) : MemberInfoService {

    override fun create(co: MemberInfoValue.MemberInfoCo) {
        val flag = memberInfoDao.create(co = co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun list(query: MemberInfoValue.MemberInfoQuery): List<MemberInfo> {
        return memberInfoDao.list(query = query)
    }

    override fun asyncUpdate(uo: MemberInfoValue.MemberInfoUo) {
        val flag = memberInfoDao.update(uo = uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}