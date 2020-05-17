//package com.onepiece.gpgaming.core.dao
//
//import com.onepiece.gpgaming.beans.model.Agent
//import com.onepiece.gpgaming.beans.value.database.AgentValue
//import com.onepiece.gpgaming.core.dao.basic.BasicDao
//
//interface AgentDao: BasicDao<Agent> {
//
//    fun findByUsername(bossId: Int, username: String): Agent?
//
//    fun create(co: AgentValue.AgentCo): Boolean
//
//    fun update(uo: AgentValue.AgentUo): Boolean
//}