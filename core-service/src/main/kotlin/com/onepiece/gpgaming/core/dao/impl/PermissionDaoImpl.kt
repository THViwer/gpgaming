package com.onepiece.gpgaming.core.dao.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Permission
import com.onepiece.gpgaming.beans.model.PermissionDetail
import com.onepiece.gpgaming.beans.value.database.PermissionUo
import com.onepiece.gpgaming.core.dao.PermissionDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PermissionDaoImpl(
        private val objectMapper: ObjectMapper
) : BasicDaoImpl<Permission>("permission"), PermissionDao {

    override val mapper: (rs: ResultSet) -> Permission
        get() = { rs ->
            val id = rs.getInt("id")
            val waiterId = rs.getInt("waiter_id")
            val permissionJson = rs.getString("permission_json")

            val javaType = objectMapper.typeFactory.constructParametricType(List::class.java, PermissionDetail::class.java)
            val permissions: List<PermissionDetail> = objectMapper.readValue(permissionJson, javaType)
            val status = rs.getString("status").let { Status.valueOf(it) }

            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Permission(id = id, waiterId = waiterId, permissions = permissions, createdTime = createdTime, status = status)
        }

    override fun findWaiterPermissions(waiterId: Int): Permission? {
        return query().where("waiter_id", waiterId)
                .executeMaybeOne(mapper)
    }

    override fun create(permissionUo: PermissionUo): Boolean {
        return insert().set("waiter_id", permissionUo.waiterId)
                .set("permission_json", objectMapper.writeValueAsString(permissionUo.permissions))
                .executeOnlyOne()
    }

    override fun update(permissionUo: PermissionUo): Boolean {
        return update().set("permission_json", objectMapper.writeValueAsString(permissionUo.permissions))
                .where("waiter_id", permissionUo.waiterId)
                .executeOnlyOne()
    }
}