package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.value.database.PlatformMemberBetUo
import com.onepiece.treasure.beans.value.database.PlatformMemberCo
import com.onepiece.treasure.beans.value.database.PlatformMemberTransferUo
import com.onepiece.treasure.beans.value.internet.web.PlatformMemberVo
import com.onepiece.treasure.core.OnePieceRedisKeyConstant
import com.onepiece.treasure.core.dao.PlatformMemberDao
import com.onepiece.treasure.core.service.PlatformMemberService
import com.onepiece.treasure.utils.RedisService
import org.springframework.stereotype.Service

@Service
class PlatformMemberServiceImpl(
        private val platformMemberDao: PlatformMemberDao,
        private val redisService: RedisService
) : PlatformMemberService {

    override fun get(id: Int): PlatformMember {
        return platformMemberDao.get(id)
    }

    override fun create(memberId: Int, platform: Platform): PlatformMemberVo {

        val platformMemberCo = PlatformMemberCo(platform = platform, memberId = memberId, username = "${platform}_$memberId",
                password = "123456")
        val id = platformMemberDao.create(platformMemberCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }

        //TODO 调用第三方平台创建账号


        redisService.delete(OnePieceRedisKeyConstant.myPlatformMembers(memberId))

        return PlatformMemberVo(memberId = memberId, platformUsername = platformMemberCo.username, platformPassword = platformMemberCo.password,
                platform = platform, id = id)
    }

    override fun myPlatforms(memberId: Int): List<PlatformMemberVo> {

        val redisKey = OnePieceRedisKeyConstant.myPlatformMembers(memberId)
        return redisService.getList(redisKey, PlatformMemberVo::class.java) {
            this.findPlatformMember(memberId).map {
                PlatformMemberVo(memberId = it.memberId, platformUsername = it.username, platformPassword = it.password, platform = it.platform,
                        id = it.id)
            }
        }

    }

    override fun findPlatformMember(memberId: Int): List<PlatformMember> {
        return platformMemberDao.findPlatformMember(memberId)
    }

//    override fun bet(platformMemberBetUo: PlatformMemberBetUo) {
//        val state = platformMemberDao.bet(platformMemberBetUo)
//        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
//    }

    override fun transferIn(platformMemberTransferUo: PlatformMemberTransferUo) {
        val state = platformMemberDao.transferIn(platformMemberTransferUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }
}