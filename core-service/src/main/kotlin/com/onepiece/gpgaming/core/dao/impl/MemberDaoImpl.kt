package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Member
import com.onepiece.gpgaming.beans.value.database.MemberCo
import com.onepiece.gpgaming.beans.value.database.MemberQuery
import com.onepiece.gpgaming.beans.value.database.MemberUo
import com.onepiece.gpgaming.core.dao.MemberDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class MemberDaoImpl: BasicDaoImpl<Member>("member"), MemberDao {

    override val mapper: (rs: ResultSet) -> Member
        get() = { rs ->
            val id = rs.getInt("id")
            val bossId = rs.getInt("boss_id")
            val clientId = rs.getInt("client_id")
            val role = rs.getString("role").let { Role.valueOf(it) }
            val agentId = rs.getInt("agent_id")
            val username = rs.getString("username")
            val name = rs.getString("name")
            val phone= rs.getString("phone")
            val password = rs.getString("password")
            val safetyPassword = rs.getString("safety_password")
            val firstPromotion = rs.getBoolean("first_promotion")
            val levelId = rs.getInt("level_id")
            val autoTransfer = rs.getBoolean("auto_transfer")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val loginIp = rs.getString("login_ip")
            val loginTime = rs.getTimestamp("login_time")?.toLocalDateTime()
            val promoteCode = rs.getString("promote_code") ?: ""
            val agencyMonthFee = rs.getBigDecimal("agency_month_fee")
            val formal = rs.getBoolean("formal")

            Member(id = id, clientId = clientId, username = username, password = password, levelId = levelId,
                    status = status, createdTime = createdTime, loginIp = loginIp, loginTime = loginTime,
                    safetyPassword = safetyPassword, name = name, phone = phone, firstPromotion = firstPromotion,
                    autoTransfer = autoTransfer, bossId = bossId, agentId = agentId, role = role, promoteCode = "$id",
                    formal = formal, agencyMonthFee = agencyMonthFee)
        }

    override fun create(memberCo: MemberCo): Int {
        return insert()
                .set("boss_id", memberCo.bossId)
                .set("client_id", memberCo.clientId)
                .set("agent_id", memberCo.agentId)
                .set("role", memberCo.role)
                .set("username", memberCo.username)
                .set("name", memberCo.name)
                .set("phone", memberCo.phone)
                .set("password", memberCo.password)
                .set("first_promotion", false)
                .set("safety_password", memberCo.safetyPassword)
                .set("level_id", memberCo.levelId)
                .set("status", Status.Normal)
                .set("id", memberCo.promoteCode)
                .set("formal", memberCo.formal)
                .set("agency_month_fee", BigDecimal.ZERO)
                .executeGeneratedKey()
    }

    override fun update(memberUo: MemberUo): Boolean {
        return update()
                .set("name", memberUo.name)
                .set("phone", memberUo.phone)
                .set("password", memberUo.password)
                .set("safety_password", memberUo.safetyPassword)
                .set("first_promotion", memberUo.firstPromotion)
                .set("status", memberUo.status)
                .set("level_id", memberUo.levelId)
                .set("login_ip", memberUo.loginIp)
                .set("login_time", memberUo.loginTime)
                .set("auto_transfer", memberUo.autoTransfer)
                .set("formal", memberUo.formal)
                .set("agency_month_fee", memberUo.agencyMonthFee)
                .where("id", memberUo.id)
                .execute() == 1

    }

    override fun getByUsername(clientId: Int, username: String): Member? {
        return query()
                .where("client_id", clientId)
                .where("username", username)
                .executeMaybeOne(mapper)
    }

    override fun getByBossIdAndUsername(bossId: Int, username: String): Member? {
        return query()
                .where("boss_id", bossId)
                .where("username", username)
                .executeMaybeOne(mapper)
    }

    override fun getByPhone(clientId: Int, phone: String): Member? {
        return query()
                .where("client_id", clientId)
                .where("phone", phone)
                .executeMaybeOne(mapper)
    }

    override fun getByBossIdAndPhone(bossId: Int, phone: String): Member? {
        return query()
                .where("boss_id", bossId)
                .where("phone", phone)
                .executeMaybeOne(mapper)
    }

    override fun findByBossIdAndCode(bossId: Int, promoteCode: String): Member? {
        return query()
                .where("boss_id", bossId)
                .where("id", promoteCode)
                .executeMaybeOne(mapper)
    }

    override fun total(query: MemberQuery): Int {
        return query("count(*) as count")
                .where("client_id", query.clientId)
                .where("role", query.role)
                .where("username", query.username)
                .where("name", query.name)
                .where("phone", query.phone)
                .where("status", query.status)
                .where("level_id", query.levelId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .count()
    }

    override fun query(query: MemberQuery, current: Int, size: Int): List<Member> {
        return query()
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("role", query.role)
                .where("agent_id", query.agentId)
                .whereIn("id", query.ids)
                .where("username", query.username)
                .where("status", query.status)
                .where("level_id", query.levelId)
                .where("id", query.promoteCode)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .sort("id desc")
                .limit(current, size)
                .execute(mapper)
    }

    override fun list(query: MemberQuery): List<Member> {
        return query()
                .where("boss_id", query.bossId)
                .where("client_id", query.clientId)
                .where("role", query.role)
                .where("agent_id", query.agentId)
                .whereIn("id", query.ids)
                .where("username", query.username)
                .where("status", query.status)
                .where("level_id", query.levelId)
                .where("id", query.promoteCode)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .sort("id desc")
                .execute(mapper)
    }

    override fun report(clientId: Int?, startDate: LocalDate, endDate: LocalDate): Map<Int, Int> {
        return query("client_id, sum(client_id) as count")
                .where("client_id", clientId)
                .asWhere("created_time >= ?", startDate)
                .asWhere("created_time < ?", endDate)
                .group("client_id")
                .execute { rs ->
                    val xClientId = rs.getInt("client_id")
                    val count = rs.getInt("count")
                    xClientId to count
                }.toMap()
    }

    override fun getLevelCount(clientId: Int): Map<Int, Int> {
        return query("level_id, count(*) as count")
                .where("client_id", clientId)
                .group("level_id")
                .execute { rs ->
                    val levelId = rs.getInt("level_id")
                    val count = rs.getInt("count")
                    levelId to count
                }.toMap()
    }

    override fun moveLevel(clientId: Int, levelId: Int, memberIds: List<Int>) {
        update()
                .set("level_id", levelId)
                .where("client_id", clientId)
                .asWhere("id in (${memberIds.joinToString(",")})")
                .execute()
    }
}
