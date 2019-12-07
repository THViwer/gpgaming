package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.value.database.PlatformMemberTransferUo
import com.onepiece.treasure.beans.value.internet.web.PlatformMemberVo
import com.onepiece.treasure.beans.value.order.BetCacheVo
import java.math.BigDecimal

interface PlatformMemberService {

    fun get(id: Int): PlatformMember

    fun create(clientId: Int, memberId: Int, platform: Platform, platformUsername: String, platformPassword: String): PlatformMemberVo

    fun updatePassword(id: Int, password: String)

    fun myPlatforms(memberId: Int): List<PlatformMemberVo>

    fun findPlatformMember(memberId: Int): List<PlatformMember>

    fun login(platform: Platform, username: String, password: String): Boolean

    fun find(memberId: Int, platform: Platform): PlatformMemberVo?

//    fun bet(platformMemberBetUo: PlatformMemberBetUo)

    fun transferIn(platformMemberTransferUo: PlatformMemberTransferUo)

    fun cleanTransferIn(memberId: Int, platform: Platform, transferOutAmount: BigDecimal = BigDecimal.ZERO)

    fun batchBet(data: List<BetCacheVo>)
}