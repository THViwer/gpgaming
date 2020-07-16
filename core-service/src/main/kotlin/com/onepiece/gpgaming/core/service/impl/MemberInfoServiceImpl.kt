package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.MemberInfo
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.core.NoRollbackException
import com.onepiece.gpgaming.core.dao.MemberDao
import com.onepiece.gpgaming.core.dao.MemberInfoDao
import com.onepiece.gpgaming.core.service.MemberInfoService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberInfoServiceImpl(
        private val memberInfoDao: MemberInfoDao,
        private val memberDao: MemberDao
) : MemberInfoService {

    override fun create(co: MemberInfoValue.MemberInfoCo) {
        val flag = memberInfoDao.create(co = co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun list(query: MemberInfoValue.MemberInfoQuery): List<MemberInfo> {
        return memberInfoDao.list(query = query)
    }

    @Transactional(rollbackFor = [NoRollbackException::class])
    override fun asyncUpdate(uo: MemberInfoValue.MemberInfoUo) {

        val has = memberInfoDao.has(memberId = uo.memberId)
        if (has == null) {
            val member = memberDao.get(id = uo.memberId)
            val co = MemberInfoValue.MemberInfoCo(bossId = member.bossId, clientId = member.clientId, agentId = member.agentId,
                    saleId = member.saleId, memberId = uo.memberId, username = member.username, registerTime = member.createdTime)
            this.create(co)
        }

        val flag = memberInfoDao.update(uo = uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun moveSale(clientId: Int, fromSaleId: Int, toSaleId: Int) {
        TODO("Not yet implemented")
    }
}