package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.PlatformMember
import com.onepiece.gpgaming.beans.value.database.PlatformMemberCo
import com.onepiece.gpgaming.beans.value.database.PlatformMemberTransferUo
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberValue
import com.onepiece.gpgaming.beans.value.internet.web.PlatformMemberVo
import com.onepiece.gpgaming.beans.value.order.BetCacheVo
import com.onepiece.gpgaming.core.OnePieceRedisKeyConstant
import com.onepiece.gpgaming.core.dao.PlatformMemberDao
import com.onepiece.gpgaming.core.service.PlatformMemberService
import com.onepiece.gpgaming.utils.RedisService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PlatformMemberServiceImpl(
        private val platformMemberDao: PlatformMemberDao,
        private val redisService: RedisService
) : PlatformMemberService {

    private val log = LoggerFactory.getLogger(PlatformMemberServiceImpl::class.java)

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    override fun get(id: Int): PlatformMember {
        log.info("platformMember, id = $id")

        try {
            return platformMemberDao.get(id)
        } catch (e: Exception) {
            log.info("platformMember, id = $id, 无法查询到该平台用户，进行请理")
            redisService.delete(OnePieceRedisKeyConstant.myPlatformMembers(id))
            throw e
        }
    }

    //    @Transactional(rollbackFor = [Exception::class], propagation = Propagation.REQUIRES_NEW)
//    @Transactional(rollbackFor = [NoRollbackException::class], propagation = Propagation.REQUIRES_NEW)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    override fun create(clientId: Int, memberId: Int, platform: Platform, platformUsername: String, platformPassword: String): PlatformMemberVo {

        log.info("开始创建db用户：$memberId, 平台：${platform}")

        val has = this.findPlatformMember(memberId = memberId).firstOrNull{ it.platform == platform }
        log.info("开始创建db用户：是否已存在已有用户：$has")
        if (has != null ) {
            redisService.delete(OnePieceRedisKeyConstant.myPlatformMembers(memberId))

            return with(has) {
                PlatformMemberVo(id = id, memberId = memberId, platform = platform, platformUsername = platformUsername, platformPassword = platformPassword)
            }
        }

        val platformMemberCo = PlatformMemberCo(platform = platform, memberId = memberId, username = platformUsername,
                password = platformPassword, clientId = clientId)
        val id = platformMemberDao.create(platformMemberCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        //TODO 调用第三方平台创建账号
        redisService.delete(OnePieceRedisKeyConstant.myPlatformMembers(memberId))

        return PlatformMemberVo(memberId = memberId, platformUsername = platformMemberCo.username, platformPassword = platformMemberCo.password,
                platform = platform, id = id)
    }

    override fun updatePassword(id: Int, password: String) {

        val platformMember = platformMemberDao.get(id)

        val state = platformMemberDao.updatePassword(id = id, password = password)
        check(state) { OnePieceExceptionCode.DATA_FAIL }


        redisService.delete(OnePieceRedisKeyConstant.myPlatformMembers(platformMember.memberId))
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    override fun myPlatforms(memberId: Int): List<PlatformMemberVo> {

        val redisKey = OnePieceRedisKeyConstant.myPlatformMembers(memberId)
        return redisService.getList(redisKey, PlatformMemberVo::class.java) {
            val list = this.findPlatformMember(memberId)

            if (list.isEmpty()) {
                emptyList<PlatformMemberVo>()
            } else {
                list.map {
                    PlatformMemberVo(memberId = it.memberId, platformUsername = it.username, platformPassword = it.password, platform = it.platform,
                            id = it.id)
                }
            }
        }
    }

    override fun find(memberId: Int, platform: Platform): PlatformMemberVo? {
        return this.myPlatforms(memberId = memberId).find { it.platform == platform }
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    override fun findPlatformMember(memberId: Int): List<PlatformMember> {
        return platformMemberDao.findPlatformMember(memberId)
    }

//    override fun bet(platformMemberBetUo: PlatformMemberBetUo) {
//        val state = platformMemberDao.bet(platformMemberBetUo)
//        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
//    }

    override fun login(platform: Platform, username: String, password: String): Boolean {
        val platformMember = platformMemberDao.findByUsername(platform = platform, username = username)
        return platformMember?.password == password
    }

    override fun transferIn(platformMemberTransferUo: PlatformMemberTransferUo) {
        val state = platformMemberDao.transferIn(platformMemberTransferUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun cleanTransferIn(memberId: Int, platform: Platform, transferOutAmount: BigDecimal) {
        val state = platformMemberDao.cleanTransferIn(memberId, platform, transferOutAmount)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun batchBet(data: List<BetCacheVo>) {
        return platformMemberDao.batchBet(data)
    }

    override fun list(query: PlatformMemberValue.PlatformMemberQuery): List<PlatformMember> {
        return platformMemberDao.list(query = query)
    }
}