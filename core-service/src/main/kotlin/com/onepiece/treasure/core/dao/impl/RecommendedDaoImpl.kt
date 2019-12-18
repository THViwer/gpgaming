package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.RecommendedType
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.Recommended
import com.onepiece.treasure.beans.value.database.RecommendedValue
import com.onepiece.treasure.core.dao.RecommendedDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class RecommendedDaoImpl: BasicDaoImpl<Recommended>("recommended"), RecommendedDao {

    override val mapper: (rs: ResultSet) -> Recommended
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val type = rs.getString("type").let { RecommendedType.valueOf(it) }
            val contentJson = rs.getString("content_json")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            Recommended(id = id, clientId = clientId, type = type, contentJson = contentJson, status = status, createdTime = createdTime)
        }

    override fun create(co: RecommendedValue.CreateVo): Boolean {
        return insert()
                .set("client_id", co.clientId)
                .set("type", co.type)
                .set("content_json", co.contentJson)
                .set("status", co.status)
                .executeOnlyOne()
    }

    override fun update(uo: RecommendedValue.UpdateVo): Boolean {
        return update()
                .set("content_json", uo.contentJson)
                .set("status", uo.status)
                .where("id", uo.id)
                .where("client_id", uo.clientId)
                .executeOnlyOne()
    }
}