package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.beans.value.database.MemberCo
import com.onepiece.treasure.beans.value.database.MemberQuery
import com.onepiece.treasure.beans.value.database.MemberUo
import com.onepiece.treasure.beans.model.Member
import java.time.LocalDate

interface MemberDao: BasicDao<Member> {

    fun create(memberCo: MemberCo): Int

    fun update(memberUo: MemberUo): Boolean

    fun getByUsername(username: String): Member?

    fun total(query: MemberQuery): Int

    fun query(query: MemberQuery, current: Int, size: Int): List<Member>

    fun list(query: MemberQuery): List<Member>

    fun report(clientId: Int?, startDate: LocalDate, endDate: LocalDate): Map<Int, Int>


}