package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Level
import com.onepiece.gpgaming.beans.value.database.LevelValue
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
            val sportRebate = rs.getBigDecimal("sport_rebate")
            val liveRebate = rs.getBigDecimal("live_rebate")
            val slotRebate = rs.getBigDecimal("slot_rebate")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            Level(id = id, clientId = clientId, name = name, status = status, createdTime = createdTime,
                    sportRebate = sportRebate, liveRebate = liveRebate, slotRebate = slotRebate)
        }

    override fun create(levelCo: LevelValue.LevelCo): Boolean {
        return insert()
                .set("client_id", levelCo.clientId)
                .set("name", levelCo.name)
                .set("sport_rebate", levelCo.sportRebate)
                .set("live_rebate", levelCo.liveRebate)
                .set("slot_rebate", levelCo.slotRebate)
                .executeOnlyOne()
    }

    override fun update(levelUo: LevelValue.LevelUo): Boolean {
        return update()
                .set("name", levelUo.name)
                .set("status", levelUo.status)
                .set("sport_rebate", levelUo.sportRebate)
                .set("live_rebate", levelUo.liveRebate)
                .set("slot_rebate", levelUo.slotRebate)
                .where("id", levelUo.id)
                .executeOnlyOne()
    }
}