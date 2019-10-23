package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.value.MemberCo
import com.onepiece.treasure.core.dao.value.MemberQuery
import com.onepiece.treasure.core.dao.value.MemberUo
import com.onepiece.treasure.core.model.Member

interface MemberDao {

    fun create(memberCo: MemberCo): Boolean

    fun update(memberUo: MemberUo): Boolean

    fun total(query: MemberQuery): Int

    fun query(query: MemberQuery, current: Int, size: Int): List<Member>

    fun list(query: MemberQuery): List<Member>

}