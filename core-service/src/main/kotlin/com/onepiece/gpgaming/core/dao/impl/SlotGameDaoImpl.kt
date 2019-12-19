package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.SlotGame
import com.onepiece.gpgaming.beans.value.database.SlotGameCo
import com.onepiece.gpgaming.beans.value.database.SlotGameUo
import com.onepiece.gpgaming.core.dao.SlotGameDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class SlotGameDaoImpl : BasicDaoImpl<SlotGame>("slot_game"), SlotGameDao {

    override val mapper: (rs: ResultSet) -> SlotGame
        get() = { rs ->
            val id = rs.getInt("id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val category = rs.getString("game_category").let { GameCategory.valueOf(it) }
            val hot = rs.getBoolean("hot")
            val new = rs.getBoolean("new")
            val gameId = rs.getString("game_id")
            val name = rs.getString("name")
            val icon = rs.getString("icon")
            val status = rs.getString("status").let{ Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            SlotGame(id = id, platform = platform, gameId = gameId, status = status, createdTime = createdTime, category = category,
                    hot = hot, new = new, name = name, icon = icon)
        }

    override fun findByPlatform(platform: Platform): List<SlotGame> {
        return query().where("platform", platform)
                .execute(mapper)
    }

    override fun create(slotGameCo: SlotGameCo): Boolean {
        return insert().set("platform", slotGameCo.platform)
                .set("game_id", slotGameCo.gameId)
                .set("category", slotGameCo.category)
                .set("hot", slotGameCo.hot)
                .set("new", slotGameCo.new)
                .set("name", slotGameCo.name)
                .set("icon", slotGameCo.icon)
                .executeOnlyOne()
    }

    override fun update(slotGameUo: SlotGameUo): Boolean {
        return update().set("platform", slotGameUo.platform)
                .set("game_id", slotGameUo.gameId)
                .set("status", slotGameUo.status)
                .set("category", slotGameUo.category)
                .set("hot", slotGameUo.hot)
                .set("new", slotGameUo.new)
                .set("name", slotGameUo.name)
                .set("icon", slotGameUo.icon)
                .where("id", slotGameUo.id)
                .executeOnlyOne()
    }
}