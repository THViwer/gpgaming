package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Level
import com.onepiece.gpgaming.beans.value.database.LevelCo
import com.onepiece.gpgaming.beans.value.database.LevelUo
import com.onepiece.gpgaming.core.dao.LevelDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
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
            val backwater = rs.getBigDecimal("backwater")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Level(id = id, clientId = clientId, name = name, status = status, createdTime = createdTime,
                    backwater = backwater)
        }

    override fun create(levelCo: LevelCo): Boolean {
        return insert()
                .set("client_id", levelCo.clientId)
                .set("name", levelCo.name)
                .set("backwater", levelCo.backwater)
                .executeOnlyOne()
    }

    override fun update(levelUo: LevelUo): Boolean {
        return update()
                .set("name", levelUo.name)
                .set("status", levelUo.status)
                .set("backwater", levelUo.backwater)
                .where("id", levelUo.id)
                .executeOnlyOne()
    }
}