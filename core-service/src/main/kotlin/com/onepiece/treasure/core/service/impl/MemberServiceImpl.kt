package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.base.Page
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Member
import com.onepiece.treasure.beans.value.database.*
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.MemberDao
import com.onepiece.treasure.core.service.MemberService
import com.onepiece.treasure.core.service.WalletService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MemberServiceImpl(
        private val memberDao: MemberDao,
        private val walletService: WalletService,
        private val redisService: RedisService
) : MemberService {

    override fun getMember(id: Int): Member {
        val redisKey = OnePieceRedisKeyConstant.member(id)
        return redisService.get(redisKey, Member::class.java) {
            memberDao.get(id)
        }!!
    }

    override fun findByIds(ids: List<Int>): List<Member> {
        val query = MemberQuery(ids = ids, clientId = null, startTime = null, endTime = null, status = null, levelId = null, username = null)
        return memberDao.list(query).toList()
    }

    override fun findByUsername(username: String?): Member? {
        if (username.isNullOrBlank()) return null
        return memberDao.getByUsername(username)?.copy(password = "", safetyPassword = "")
    }

    override fun query(memberQuery: MemberQuery, current: Int, size: Int): Page<Member> {
        val total = memberDao.total(query = memberQuery)
        if (total == 0) return Page.empty()

        val data = memberDao.query(memberQuery, 0, 10).map { it.copy(password = "") }
        return Page.of(total = total, data = data)
    }

    override fun login(loginValue: LoginValue): Member {

        // check username and password
        val member  = memberDao.getByUsername(loginValue.username)
        checkNotNull(member) { OnePieceExceptionCode.LOGIN_FAIL}
        check(loginValue.password == member.password) { OnePieceExceptionCode.LOGIN_FAIL }
        check(member.status == Status.Normal) { OnePieceExceptionCode.USER_STOP }

        // update user for loginIp and loginTime
        val memberUo = MemberUo(id = member.id, loginIp = loginValue.ip, loginTime = LocalDateTime.now())
        this.update(memberUo)

        return member.copy(password = "")
    }

    override fun checkSafetyPassword(id: Int, safetyPassword: String) {
        val member = memberDao.get(id)
        check(member.safetyPassword == safetyPassword) { OnePieceExceptionCode.SAFETY_PASSWORD_CHECK_FAIL }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun create(memberCo: MemberCo) {

        // check username exist
        val hasMember = memberDao.getByUsername(memberCo.username)
        check(hasMember == null) { OnePieceExceptionCode.USERNAME_EXISTENCE }

        // create member
        val id = memberDao.create(memberCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // create wallet
        val walletCo = WalletCo(clientId = memberCo.clientId, memberId = id)
        walletService.create(walletCo)
    }

    override fun update(memberUo: MemberUo) {
        val member = this.getMember(memberUo.id)
        if (memberUo.oldPassword != null) {
            check(memberUo.oldPassword == member.password) { OnePieceExceptionCode.PASSWORD_FAIL }
        }
        if (memberUo.oldSafetyPassword != null) {
            check(memberUo.oldPassword == member.password) { OnePieceExceptionCode.SAFETY_PASSWORD_FAIL }
        }

        val state = memberDao.update(memberUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.member(memberUo.id))
    }
}