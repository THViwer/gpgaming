package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.Member
import com.onepiece.treasure.beans.value.database.MemberCo
import com.onepiece.treasure.beans.value.database.MemberQuery
import com.onepiece.treasure.beans.value.database.MemberUo
import com.onepiece.treasure.core.dao.MemberDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class MemberDaoImpl: BasicDaoImpl<Member>("member"), MemberDao {

    override val mapper: (rs: ResultSet) -> Member
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val username = rs.getString("username")
            val name = rs.getString("name")
            val phone= rs.getString("phone")
            val password = rs.getString("password")
            val safetyPassword = rs.getString("safety_password")
            val levelId = rs.getInt("level_id")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val loginIp = rs.getString("login_ip")
            val loginTime = rs.getTimestamp("login_time")?.toLocalDateTime()

            Member(id = id, clientId = clientId, username = username, password = password, levelId = levelId,
                    status = status, createdTime = createdTime, loginIp = loginIp, loginTime = loginTime,
                    safetyPassword = safetyPassword, name = name, phone = phone)
        }

    override fun create(memberCo: MemberCo): Int {
        return insert()
                .set("client_id", memberCo.clientId)
                .set("username", memberCo.username)
                .set("name", memberCo.name)
                .set("phone", memberCo.phone)
                .set("password", memberCo.password)
                .set("safety_password", memberCo.safetyPassword)
                .set("level_id", memberCo.levelId)
                .set("status", Status.Normal)
                .executeGeneratedKey()
    }

    override fun update(memberUo: MemberUo): Boolean {
        return update()
                .set("name", memberUo.name)
                .set("phone", memberUo.phone)
                .set("password", memberUo.password)
                .set("safety_password", memberUo.safetyPassword)
                .set("status", memberUo.status)
                .set("level_id", memberUo.levelId)
                .set("login_ip", memberUo.loginIp)
                .set("login_time", memberUo.loginTime)
                .where("id", memberUo.id)
                .execute() == 1

    }

    override fun getByUsername(username: String): Member? {
        return query().where("username", username)
                .executeMaybeOne(mapper)
    }

    override fun total(query: MemberQuery): Int {
        return query("count(*) as count")
                .where("client_id", query.clientId)
                .where("username", query.username)
                .where("status", query.status)
                .where("level_id", query.levelId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .count()
    }

    override fun query(query: MemberQuery, current: Int, size: Int): List<Member> {
        return query()
                .where("client_id", query.clientId)
                .where("username", query.username)
                .where("status", query.status)
                .where("level_id", query.levelId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .limit(current, size)
                .execute(mapper)
    }

    override fun list(query: MemberQuery): List<Member> {
        return query()
                .where("client_id", query.clientId)
                .whereIn("id", query.ids)
                .where("username", query.username)
                .where("status", query.status)
                .where("level_id", query.levelId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .execute(mapper)
    }

    override fun report(clientId: Int?, startDate: LocalDate, endDate: LocalDate): Map<Int, Int> {
        return query("client_id, sum(client_id) as count")
                .where("client_id", clientId)
                .asWhere("created_time >= ?", startDate)
                .asWhere("created_time < ?", endDate)
                .group("client_id")
                .execute { rs ->
                    val clientId = rs.getInt("client_id")
                    val count = rs.getInt("count")
                    clientId to count
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
