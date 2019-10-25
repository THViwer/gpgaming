package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.MemberDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.beans.value.database.MemberCo
import com.onepiece.treasure.beans.value.database.MemberQuery
import com.onepiece.treasure.beans.value.database.MemberUo
import com.onepiece.treasure.beans.model.Member
import com.onepiece.treasure.beans.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class MemberDaoImpl: BasicDaoImpl<Member>("member"), MemberDao {

    override fun mapper(): (rs: ResultSet) -> Member {
        return { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val username = rs.getString("username")
            val password = rs.getString("password")
            val levelId = rs.getInt("level_id")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val loginIp = rs.getString("login_ip")
            val loginTime = rs.getTimestamp("login_time")?.toLocalDateTime()

            Member(id = id, clientId = clientId, username = username, password = password, levelId = levelId,
                    status = status, createdTime = createdTime, loginIp = loginIp, loginTime = loginTime)
        }
    }

    override fun create(memberCo: MemberCo): Boolean {
        return insert()
                .set("client_id", memberCo.clientId)
                .set("username", memberCo.username)
                .set("password", memberCo.password)
                .set("level_id", memberCo.levelId)
                .execute() == 1
    }

    override fun update(memberUo: MemberUo): Boolean {
        return update()
                .set("password", memberUo.password)
                .set("status", memberUo.status)
                .set("level_id", memberUo.levelId)
                .asWhere("id", memberUo.id)
                .execute() == 1

    }

    override fun getByUsername(username: String): Member? {
        return query().where("username", username)
                .executeMaybeOne(mapper())
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
                .execute(mapper())
    }

    override fun list(query: MemberQuery): List<Member> {
        return query()
                .where("client_id", query.clientId)
                .where("username", query.username)
                .where("status", query.status)
                .where("level_id", query.levelId)
                .asWhere("created_time > ?", query.startTime)
                .asWhere("created_time <= ?", query.endTime)
                .execute(mapper())
    }

}
