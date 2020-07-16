package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.base.Page
import com.onepiece.gpgaming.beans.model.Member
import com.onepiece.gpgaming.beans.value.database.LoginValue
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberUo

interface MemberService {

    fun getAgentByCode(bossId: Int, clientId: Int, code: String): Member?

    fun getDefaultAgent(bossId: Int): Member

    fun getMember(id: Int): Member

    fun findByIds(ids: List<Int>, levelId: Int? = null): List<Member>

    fun findByUsername(clientId: Int, username: String?): Member?

    fun findByBossIdAndUsername(bossId: Int, username: String?): Member?

    fun findByPhone(clientId: Int, phone: String?): Member?

    fun findByBossIdAndPhone(bossId: Int, phone: String?): Member?

    fun findByBossIdAndCode(bossId: Int, promoteCode: String): Member?

    fun list(memberQuery: MemberQuery): List<Member>

    fun query(memberQuery: MemberQuery, current: Int, size: Int): Page<Member>

    fun login(loginValue: LoginValue): Member

    fun checkSafetyPassword(id: Int, safetyPassword: String)

    fun create(memberCo: MemberCo): Int

    fun update(memberUo: MemberUo)

    fun getLevelCount(clientId: Int): Map<Int, Int>

    fun moveLevel(clientId: Int, levelId: Int, memberIds: List<Int>)

    fun moveSale(clientId: Int, fromSaleId: Int, toSaleId: Int)

}