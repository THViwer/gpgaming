//package com.onepiece.gpgaming.core.dao.impl
//
//import com.onepiece.gpgaming.beans.enums.Status
//import com.onepiece.gpgaming.beans.model.Agent
//import com.onepiece.gpgaming.beans.value.database.AgentValue
//import com.onepiece.gpgaming.core.dao.AgentDao
//import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
//import org.springframework.stereotype.Repository
//import java.sql.ResultSet
//
//@Repository
//class AgentDaoImpl : BasicDaoImpl<Agent>("agent"), AgentDao {
//
//    override val mapper: (rs: ResultSet) -> Agent
//        get() = { rs ->
//            val id = rs.getInt("id")
//            val bossId = rs.getInt("boss_id")
//            val username = rs.getString("username")
//            val password = rs.getString("password")
//            val status = rs.getString("status").let { Status.valueOf(it) }
//            val code = rs.getString("code")
//            val loginTime = rs.getTimestamp("login_time").toLocalDateTime()
//            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
//
//            Agent(id = id, bossId = bossId, username = username, password = password, status = status, code = code,
//            loginTime = loginTime, createdTime = createdTime)
//
//        }
//
//    override fun findByUsername(bossId: Int, username: String): Agent? {
//
//        return query()
//                .where("boss_id", bossId)
//                .where("username", username)
//                .executeMaybeOne(mapper)
//    }
//
//    override fun create(co: AgentValue.AgentCo): Boolean {
//        return insert()
//                .set("boss_id", co.bossId)
//                .set("username", co.username)
//                .set("password", co.password)
//                .set("status", co.status)
//                .set("code", co.code)
//                .set("login_time", co.loginTime)
//                .executeOnlyOne()
//    }
//
//    override fun update(uo: AgentValue.AgentUo): Boolean {
//        return update()
//                .set("password", uo.password)
//                .set("status", uo.status)
//                .set("code", uo.code)
//                .set("login_time", uo.loginTime)
//                .executeOnlyOne()
//    }
//}