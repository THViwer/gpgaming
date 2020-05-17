package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Member
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberRelationValue
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.beans.value.database.WalletCo
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.MemberDao
import com.onepiece.gpgaming.core.dao.MemberRelationDao
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MemberServiceImpl(
        private val memberDao: MemberDao,
        private val walletService: WalletService,
        private val redisService: RedisService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val memberRelationDao: MemberRelationDao
) : MemberService {

    override fun getDefaultAgent(bossId: Int): Member {
        return memberDao.getByBossIdAndUsername(bossId = bossId, username = "default_agent")!!
    }

    override fun getMember(id: Int): Member {
        val redisKey = OnePieceRedisKeyConstant.member(id)
        return redisService.get(redisKey, Member::class.java) {
            memberDao.get(id)
        }!!
    }

    override fun findByIds(ids: List<Int>, levelId: Int?): List<Member> {
        val query = MemberQuery(ids = ids, clientId = null, startTime = null, endTime = null, status = null, levelId = levelId,
                username = null, promoteCode = null, name = null, phone = null, role = null, agentId = null, bossId = null)
        return memberDao.list(query).toList()
    }

    override fun findByUsername(clientId: Int, username: String?): Member? {
        if (username.isNullOrBlank()) return null
        return memberDao.getByUsername(clientId, username)?.copy(password = "", safetyPassword = "")
    }

    override fun findByBossIdAndUsername(bossId: Int, username: String?): Member? {
        if (username.isNullOrBlank()) return null
        return memberDao.getByBossIdAndUsername(bossId, username)?.copy(password = "", safetyPassword = "")    }

    override fun findByPhone(clientId: Int, phone: String?): Member? {
        if (phone.isNullOrBlank()) return null

        return memberDao.getByPhone(clientId, phone)?.copy(password = "", safetyPassword = "")
    }

    override fun findByBossIdAndPhone(bossId: Int, phone: String?): Member? {
        if (phone.isNullOrBlank()) return null

        return memberDao.getByBossIdAndPhone(bossId, phone)?.copy(password = "", safetyPassword = "")
    }

    override fun findByBossIdAndCode(bossId: Int, promoteCode: String): Member? {
        if (promoteCode.isNullOrBlank()) return null

        return memberDao.getByBossIdAndPhone(bossId, promoteCode)?.copy(password = "", safetyPassword = "")
    }

    override fun query(memberQuery: MemberQuery, current: Int, size: Int): Page<Member> {
        val total = memberDao.total(query = memberQuery)
        if (total == 0) return Page.empty()

        val data = memberDao.query(memberQuery, current, size).map { it.copy(password = "") }
        return Page.of(total = total, data = data)
    }

    override fun login(loginValue: LoginValue): Member {

        // check username and password
        val member  = memberDao.getByBossIdAndUsername(loginValue.bossId, loginValue.username)
        checkNotNull(member) { OnePieceExceptionCode.LOGIN_FAIL}
        check(bCryptPasswordEncoder.matches(loginValue.password, member.password)) { OnePieceExceptionCode.LOGIN_FAIL }
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
        val hasMember = memberDao.getByUsername(memberCo.clientId, memberCo.username)
        check(hasMember == null) { OnePieceExceptionCode.USERNAME_EXISTENCE }

        // create member
        val password = bCryptPasswordEncoder.encode(memberCo.password)
        val id = memberDao.create(memberCo.copy(password = password))
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // create wallet
        val walletCo = WalletCo(clientId = memberCo.clientId, memberId = id)
        walletService.create(walletCo)

        // 创建代理关系
        val agent = memberDao.get(id = memberCo.agentId)
        val (r1, r2) = if (agent.username == "default_agent") {
            agent.id to null
        } else {
            val preAgent = memberDao.get(id = agent.agentId)
            preAgent.id to agent.id
        }

        val relationCo = MemberRelationValue.MemberRelationCo(bossId = memberCo.bossId, memberId = id, r1 = r1, r2 = r2)
        memberRelationDao.create(relationCo)
    }

    override fun update(memberUo: MemberUo) {
        val member = this.getMember(memberUo.id)

        if (memberUo.oldPassword != null) {
            check(bCryptPasswordEncoder.matches(memberUo.oldPassword, member.password)) { OnePieceExceptionCode.PASSWORD_FAIL }
        }
        if (memberUo.oldSafetyPassword != null) {
            check(memberUo.oldSafetyPassword == member.safetyPassword) { OnePieceExceptionCode.SAFETY_PASSWORD_FAIL }
        }

        val password = memberUo.password?.let {
            bCryptPasswordEncoder.encode(it)
        }

        val state = memberDao.update(memberUo.copy(password = password))
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        redisService.delete(OnePieceRedisKeyConstant.member(memberUo.id))
    }

    override fun getLevelCount(clientId: Int): Map<Int, Int> {
        return memberDao.getLevelCount(clientId)
    }

    override fun moveLevel(clientId: Int, levelId: Int, memberIds: List<Int>) {
        return memberDao.moveLevel(clientId, levelId, memberIds)
    }
}