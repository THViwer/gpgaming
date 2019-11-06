package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.enums.Platform
import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.value.database.PlatformMemberBetUo
import com.onepiece.treasure.beans.value.database.PlatformMemberTransferUo
import com.onepiece.treasure.beans.value.internet.web.PlatformMemberVo

interface PlatformMemberService {

    fun get(id: Int): PlatformMember

    fun create(memberId: Int, platform: Platform, platformUsername: String, platformPassword: String): PlatformMemberVo

    fun myPlatforms(memberId: Int): List<PlatformMemberVo>

    fun findPlatformMember(memberId: Int): List<PlatformMember>

//    fun bet(platformMemberBetUo: PlatformMemberBetUo)

    fun transferIn(platformMemberTransferUo: PlatformMemberTransferUo)

}