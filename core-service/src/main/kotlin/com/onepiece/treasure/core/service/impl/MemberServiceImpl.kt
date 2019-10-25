package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Member
import com.onepiece.treasure.beans.value.database.MemberCo
import com.onepiece.treasure.beans.value.database.MemberQuery
import com.onepiece.treasure.beans.value.database.MemberUo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.MemberDao
import com.onepiece.treasure.core.service.MemberService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class MemberServiceImpl(
        private val memberDao: MemberDao,
        private val redisService: RedisService
) : MemberService {

    override fun findById(id: Int): Member {
        val redisKey = OnePieceRedisKeyConstant.member(id)
        return redisService.get(redisKey, Member::class.java) {
            memberDao.get(id)
        }!!
    }

    override fun query(memberQuery: MemberQuery, current: Int, size: Int): Page<Member> {
        val total = memberDao.total(query = memberQuery)
        if (total == 0) return Page.empty()

        val data = memberDao.query(memberQuery, 0, 10)
        return Page.of(total = total, data = data)
    }

    override fun create(memberCo: MemberCo) {

        val state = memberDao.create(memberCo)
        check(state) { OnePieceExceptionCode.DB_CHANGE }
    }

    override fun update(memberUo: MemberUo) {
        val state = memberDao.update(memberUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE }
    }
}