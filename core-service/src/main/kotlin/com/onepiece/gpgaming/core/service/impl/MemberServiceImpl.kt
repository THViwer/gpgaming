package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.SaleScope
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Member
import com.onepiece.gpgaming.beans.value.database.LoginHistoryValue
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberInfoValue
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.beans.value.database.WalletCo
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.MemberDao
import com.onepiece.gpgaming.core.dao.MemberRelationDao
import com.onepiece.gpgaming.core.risk.RiskUtil
import com.onepiece.gpgaming.core.service.LoginHistoryService
import com.onepiece.gpgaming.core.service.MemberInfoService
import com.onepiece.gpgaming.core.service.MemberService
import com.onepiece.gpgaming.core.service.WaiterService
import com.onepiece.gpgaming.core.service.WalletService
import com.onepiece.gpgaming.utils.RedisService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class MemberServiceImpl(
        private val memberDao: MemberDao,
        private val redisService: RedisService,
        private val bCryptPasswordEncoder: BCryptPasswordEncoder,
        private val memberRelationDao: MemberRelationDao,
        private val waiterService: WaiterService,
        private val memberInfoService: MemberInfoService,
        private val loginHistoryService: LoginHistoryService
) : MemberService {

    @Autowired
    lateinit var riskUtil: RiskUtil

    @Autowired
    lateinit var walletService: WalletService


    override fun getAgentByCode(bossId: Int, clientId: Int, code: String): Member? {

        val query = MemberQuery(bossId = bossId, clientId = clientId,  role = Role.Agent, username = null, name = null, phone = null,
                levelId = null, promoteCode = code, startTime = null, status = null, agentId = null, endTime = null)
        return memberDao.list(query).firstOrNull()
    }

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

        return memberDao.findByBossIdAndCode(bossId, promoteCode)?.copy(password = "", safetyPassword = "")
    }

    override fun list(memberQuery: MemberQuery): List<Member> {
        return memberDao.query(memberQuery, 0, 5000).map { it.copy(password = "") }
    }

    override fun query(memberQuery: MemberQuery, current: Int, size: Int): Page<Member> {
        val total = memberDao.total(query = memberQuery)
        if (total == 0) return Page.empty()

        val data = memberDao.query(memberQuery, current, size).map { it.copy(password = "") }
        return Page.of(total = total, data = data)
    }

    override fun login(loginValue: LoginValue, deviceType: String): Member {

        // check username and password
        val member  = memberDao.getByBossIdAndUsername(loginValue.bossId, loginValue.username)
        checkNotNull(member) { OnePieceExceptionCode.LOGIN_FAIL}
        check(bCryptPasswordEncoder.matches(loginValue.password, member.password)) { OnePieceExceptionCode.LOGIN_FAIL }
        check(member.status == Status.Normal) { OnePieceExceptionCode.USER_STOP }

        // update user for loginIp and loginTime
        val memberUo = MemberUo(id = member.id, loginIp = loginValue.ip, loginTime = LocalDateTime.now())
        this.update(memberUo)

        // 更新会员信息
        val infoUo = MemberInfoValue.MemberInfoUo.ofLogin(memberId = member.id)
        memberInfoService.asyncUpdate(uo = infoUo)

        // 登陆历史
        val historyCo = LoginHistoryValue.LoginHistoryCo(bossId = member.bossId, clientId = member.clientId, userId = member.id,
                role = Role.Member, ip = loginValue.ip, country = "", username = loginValue.username, deviceType = deviceType)
        loginHistoryService.create(historyCo)

        return member.copy(password = "")
    }

    override fun checkSafetyPassword(id: Int, safetyPassword: String) {
        val member = memberDao.get(id)
        check(member.safetyPassword == safetyPassword) { OnePieceExceptionCode.SAFETY_PASSWORD_CHECK_FAIL }
    }

    private fun getAgentSequence(): Long {
        val sequence = redisService.increase("agent:sequence")

        return if (sequence < 1) {
            try {
                val memberQuery = MemberQuery(role = Role.Agent)
                val member = memberDao.query(query = memberQuery, current = 0, size = 1).firstOrNull()
                member?.promoteCode?.toLong() ?: 1
            } catch (e: Exception) {
                1L
            }
        } else {
            sequence
        }
    }

    private fun getPromoteCode(): String {
        val sequence = this.getAgentSequence()
        return when {
            sequence < 10 -> "0000$sequence"
            sequence < 100 -> "000$sequence"
            sequence < 1000 -> "00$sequence"
            sequence < 10000 -> "0${sequence}"
            else -> "$sequence"
        }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun create(memberCo: MemberCo): Int {


        // check username exist
        val hasMember = memberDao.getByUsername(memberCo.clientId, memberCo.username)
        check(hasMember == null) { OnePieceExceptionCode.USERNAME_EXISTENCE }

        val promoteCode = this.getPromoteCode()

        // 电销人员Id
        val saleId = waiterService.selectSale(bossId = memberCo.bossId, clientId = memberCo.clientId, saleId = memberCo.saleId)
                ?.id ?: -1
        val saleScope = if (memberCo.saleId == saleId && saleId != -1) SaleScope.Own else SaleScope.System

        // create member
        val riskLevel = riskUtil.checkRiskLevel(clientId = memberCo.clientId, username = memberCo.username, name = memberCo.name, ip = memberCo.registerIp)
        val password = bCryptPasswordEncoder.encode(memberCo.password)
        val id = memberDao.create(memberCo.copy(password = password, promoteCode = promoteCode, saleId = saleId, saleScope = saleScope, riskLevel = riskLevel))
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        // create wallet
        val walletCo = WalletCo(clientId = memberCo.clientId, memberId = id)
        walletService.create(walletCo)

        // 创建会员信息
        val memberInfoCo = MemberInfoValue.MemberInfoCo(bossId = memberCo.bossId, clientId = memberCo.clientId, agentId = memberCo.agentId, saleId = saleId,
                memberId = id, username = memberCo.username)
        memberInfoService.create(memberInfoCo)

        return id

        // 创建代理关系
//        val agent = memberDao.get(id = memberCo.agentId)
//        val (r1, r2) = if (agent.username == "default_agent") {
//            agent.id to null
//        } else {
//            val preAgent = memberDao.get(id = agent.agentId)
//            preAgent.id to agent.id
//        }
//
//        val relationCo = MemberRelationValue.MemberRelationCo(bossId = memberCo.bossId, memberId = id, r1 = r1, r2 = r2)
//        memberRelationDao.create(relationCo)
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

        // 修改电销Id
        if (memberUo.saleId != null) {
            val infoUo = MemberInfoValue.MemberInfoUo.ofUpdateSale(memberId = member.id, saleId = memberUo.saleId!!)
            memberInfoService.asyncUpdate(uo = infoUo)
        }

        redisService.delete(OnePieceRedisKeyConstant.member(memberUo.id))
    }

    override fun getLevelCount(clientId: Int): Map<Int, Int> {
        return memberDao.getLevelCount(clientId)
    }

    override fun moveLevel(clientId: Int, levelId: Int, memberIds: List<Int>) {
        return memberDao.moveLevel(clientId, levelId, memberIds)
    }

    override fun moveSale(clientId: Int, fromSaleId: Int, toSaleId: Int) {
        memberDao.moveSale(clientId = clientId, fromSaleId = fromSaleId, toSaleId = toSaleId)

        memberInfoService
    }
}