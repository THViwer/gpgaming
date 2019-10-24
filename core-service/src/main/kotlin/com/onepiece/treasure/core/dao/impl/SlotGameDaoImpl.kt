package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.SlotGameDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.core.dao.value.SlotGameCo
import com.onepiece.treasure.core.dao.value.SlotGameUo
import com.onepiece.treasure.core.model.SlotGame
import com.onepiece.treasure.core.model.enums.Platform
import com.onepiece.treasure.core.model.enums.Status
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class SlotGameDaoImpl : BasicDaoImpl<SlotGame>("slot_game"), SlotGameDao {

    override fun mapper(): (rs: ResultSet) -> SlotGame {
        return { rs ->
            val id = rs.getInt("id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val gameId = rs.getString("game_id")
            val status = rs.getString("status").let{ Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            SlotGame(id = id, platform = platform, gameId = gameId, status = status, createdTime = createdTime)
        }
    }

    override fun create(slotGameCo: SlotGameCo): Boolean {
        return insert().set("platform", slotGameCo.platform)
                .set("gameId", slotGameCo.gameId)
                .executeOnlyOne()
    }

    override fun update(slotGameUo: SlotGameUo): Boolean {
        return update().set("platform", slotGameUo.platform)
                .set("game_id", slotGameUo.gameId)
                .set("status", slotGameUo.status)
                .where("id", slotGameUo.id)
                .executeOnlyOne()
    }
}