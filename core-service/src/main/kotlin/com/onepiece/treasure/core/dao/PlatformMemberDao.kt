package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.model.PlatformMember
import com.onepiece.treasure.beans.value.database.PlatformMemberBetUo
import com.onepiece.treasure.beans.value.database.PlatformMemberCo
import com.onepiece.treasure.beans.value.database.PlatformMemberTransferUo
import com.onepiece.treasure.core.dao.basic.BasicDao

interface PlatformMemberDao: BasicDao<PlatformMember> {

    fun findPlatformMember(memberId: Int): List<PlatformMember>

    fun create(platformMemberCo: PlatformMemberCo): Boolean

    fun bet(platformMemberBetUo: PlatformMemberBetUo): Boolean

    fun transferIn(platformMemberTransferUo: PlatformMemberTransferUo): Boolean

}