package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.SaleScope
import com.onepiece.gpgaming.beans.model.Member
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.core.dao.basic.BasicDao
import java.time.LocalDate

interface MemberDao: BasicDao<Member> {

    fun create(memberCo: MemberCo): Int

    fun update(memberUo: MemberUo): Boolean

    fun getByUsername(clientId: Int, username: String): Member?

    fun getByBossIdAndUsername(bossId: Int, username: String): Member?

    fun getByPhone(clientId: Int, phone: String): Member?

    fun getByBossIdAndPhone(bossId: Int, phone: String): Member?

    fun findByBossIdAndCode(bossId: Int, promoteCode: String): Member?

    fun total(query: MemberQuery): Int

    fun saleCount(saleId: Int?, startDate: LocalDate, endDate: LocalDate, scope: SaleScope): Map<Int, Int>

    fun query(query: MemberQuery, current: Int, size: Int): List<Member>

    fun list(query: MemberQuery): List<Member>

    fun report(clientId: Int?, startDate: LocalDate, endDate: LocalDate): Map<Int, Int>

    fun getLevelCount(clientId: Int): Map<Int, Int>

    fun introduceCount(clientId: Int,  memberId:  Int): Int

    fun moveLevel(clientId: Int, levelId: Int, memberIds: List<Int>)

    fun moveSale(clientId: Int, fromSaleId: Int, toSaleId: Int)

}