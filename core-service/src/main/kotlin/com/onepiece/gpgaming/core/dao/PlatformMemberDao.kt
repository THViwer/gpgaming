package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.PlatformMember
import com.onepiece.gpgaming.beans.value.database.PlatformMemberCo
import com.onepiece.gpgaming.beans.value.database.PlatformMemberTransferUo
import com.onepiece.gpgaming.beans.value.order.BetCacheVo
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.math.BigDecimal

interface PlatformMemberDao: BasicDao<PlatformMember> {

    fun findPlatformMember(memberId: Int): List<PlatformMember>

    fun findByUsername(platform: Platform, username: String): PlatformMember

    fun create(platformMemberCo: PlatformMemberCo): Int

    fun transferIn(platformMemberTransferUo: PlatformMemberTransferUo): Boolean

    fun cleanTransferIn(memberId: Int, platform: Platform, transferOutAmount: BigDecimal): Boolean

    fun batchBet(data: List<BetCacheVo>)

    fun updatePassword(id: Int, password: String): Boolean

}