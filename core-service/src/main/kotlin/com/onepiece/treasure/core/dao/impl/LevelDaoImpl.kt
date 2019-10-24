package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.LevelDao
import com.onepiece.treasure.core.dao.basic.BasicDao
import com.onepiece.treasure.core.dao.value.LevelCo
import com.onepiece.treasure.core.dao.value.LevelUo
import com.onepiece.treasure.core.model.Level
import com.onepiece.treasure.core.model.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class LevelDaoImpl: BasicDao<Level>("level"), LevelDao {

    override fun mapper(): (rs: ResultSet) -> Level {

        return { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val name = rs.getString("name")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Level(id = id, clientId = clientId, name = name, status = status, createdTime = createdTime)
        }

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