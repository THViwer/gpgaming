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

    override fun get(memberId: Int): MemberInfo {
        val has = memberInfoDao.has(memberId = memberId)
        if (has == null) {
            val member = memberDao.get(id = memberId)
            val co = MemberInfoValue.MemberInfoCo(bossId = member.bossId, clientId = member.clientId, agentId = member.agentId,
                    saleId = member.saleId, memberId = memberId, username = member.username, registerTime = member.createdTime)
            this.create(co)

            return this.get(memberId = memberId)
        }

        return  has
    }

    override fun create(co: MemberInfoValue.MemberInfoCo) {
        val flag = memberInfoDao.create(co = co)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun count(query: MemberInfoValue.MemberCountQuery): Int {
        return memberInfoDao.count(query)
    }

    override fun list(query: MemberInfoValue.MemberInfoQuery): List<MemberInfo> {
        return memberInfoDao.list(query = query)
    }

    @Transactional(rollbackFor = [NoRollbackException::class])
    override fun asyncUpdate(uo: MemberInfoValue.MemberInfoUo) {

        val info = this.get(memberId = uo.memberId)

        val flag = memberInfoDao.update(uo = uo)
        check(flag) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

//    override fun moveSale(clientId: Int, fromSaleId: Int, toSaleId: Int) {
//        TODO("Not yet implemented")
//    }
}