package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.LevelDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.beans.value.database.LevelCo
import com.onepiece.treasure.beans.value.database.LevelUo
import com.onepiece.treasure.beans.model.Level
import com.onepiece.treasure.beans.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class LevelDaoImpl: BasicDaoImpl<Level>("level"), LevelDao {

    override val mapper: (rs: ResultSet) -> Level
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val name = rs.getString("name")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Level(id = id, clientId = clientId, name = name, status = status, createdTime = createdTime)
        }

    override fun create(levelCo: LevelCo): Boolean {
        return insert()
                .set("client_id", levelCo.clientId)
                .set("name", levelCo.name)
                .executeOnlyOne()
    }

    override fun update(levelUo: LevelUo): Boolean {
        return update()
                .set("name", levelUo.name)
                .set("status", levelUo.status)
                .where("id", levelUo.id)
                .executeOnlyOne()
    }
}