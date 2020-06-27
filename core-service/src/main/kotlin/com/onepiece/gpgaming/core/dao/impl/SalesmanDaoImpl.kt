//package com.onepiece.gpgaming.core.dao.impl
//
//import com.onepiece.gpgaming.beans.enums.Status
//import com.onepiece.gpgaming.beans.model.Salesman
//import com.onepiece.gpgaming.beans.value.database.SalesmanValue
//import com.onepiece.gpgaming.core.dao.SalesmanDao
//import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
//import org.springframework.stereotype.Repository
//import java.sql.ResultSet
//
//@Repository
//class SalesmanDaoImpl: BasicDaoImpl<Salesman>("salesman"), SalesmanDao {
//
//    override val mapper: (rs: ResultSet) -> Salesman
//        get() = { rs ->
//
//            val id = rs.getInt("id")
//            val bossId = rs.getInt("boss_id")
//            val clientId = rs.getInt("client_id")
//            val username = rs.getString("username")
//            val password = rs.getString("password")
//            val ownCustomerScale = rs.getBigDecimal("own_customer_scale")
//            val systemCustomerScale = rs.getBigDecimal("system_customer_scale")
//            val status = rs.getString("status").let { Status.valueOf(it) }
//            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
//
//            Salesman(id = id, bossId = bossId, clientId = clientId, username = username, password = password,
//                    ownCustomerScale = ownCustomerScale, systemCustomerScale = systemCustomerScale, status = status, createdTime = createdTime)
//        }
//
//    override fun create(co: SalesmanValue.SalesmanCo): Boolean {
//        return insert()
//                .set("boss_id", co.bossId)
//                .set("client_id", co.clientId)
//                .set("username", co.username)
//                .set("password", co.password)
//                .set("own_customer_scale", co.ownCustomerScale)
//                .set("system_customer_scale", co.systemCustomerScale)
//                .set("status", Status.Normal)
//                .executeOnlyOne()
//    }
//
//    override fun update(uo: SalesmanValue.SalesmanUo): Boolean {
//        return update()
//                .set("own_customer_scale", uo.ownCustomerScale)
//                .set("system_customer_scale", uo.systemCustomerScale)
//                .set("status", Status.Normal)
//                .where("id", uo.id)
//                .executeOnlyOne()
//    }
//
//    override fun list(query: SalesmanValue.SalesmanQuery): List<Salesman> {
//        return query()
//                .where("boss_id", query.bossId)
//                .where("client_id", query.clientId)
//                .where("username", query.username)
//                .execute(mapper)
//    }
//}