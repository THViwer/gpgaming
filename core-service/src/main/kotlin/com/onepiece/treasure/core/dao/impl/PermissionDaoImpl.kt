package com.onepiece.treasure.core.dao.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.core.dao.PermissionDao
import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.PermissionCo
import com.onepiece.treasure.core.dao.value.PermissionUo
import com.onepiece.treasure.core.model.Permission
import com.onepiece.treasure.core.model.PermissionDetail
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PermissionDaoImpl(
        private val objectMapper: ObjectMapper
) : BasicDao<Permission>("permission"), PermissionDao {

    override fun mapper(): (rs: ResultSet) -> Permission {

        return { rs ->
            val id = rs.getInt("id")
            val waiterId = rs.getInt("waiter_id")
            val permissionJson = rs.getString("permission_json")

            val javaType = objectMapper.typeFactory.constructParametricType(List::class.java, PermissionDetail::class.java)
            val permissions: List<PermissionDetail> = objectMapper.readValue(permissionJson, javaType)

            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Permission(id = id, waiterId = waiterId, permissions = permissions, createdTime = createdTime)
        }

    }

    override fun create(permissionCo: PermissionCo): Boolean {
        return insert().set("waiter_id", permissionCo.waiterId)
                .set("permission_json", objectMapper.writeValueAsString(permissionCo.permissions))
                .executeOnlyOne()
    }

    override fun update(permissionUo: PermissionUo): Boolean {
        return update().set("permission_json", objectMapper.writeValueAsString(permissionUo.permissions))
                .where("waiter_id", permissionUo.waiterId)
                .executeOnlyOne()
    }
}