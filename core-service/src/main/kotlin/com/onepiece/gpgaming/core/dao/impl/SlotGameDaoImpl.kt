package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.SlotGame
import com.onepiece.gpgaming.beans.value.database.SlotGameValue
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
            val category = rs.getString("category").let { GameCategory.valueOf(it) }
            val hot = rs.getBoolean("hot")
            val new = rs.getBoolean("new")
            val gameId = rs.getString("game_id")
            val launchs = rs.getString("launchs").split(",").map {
                LaunchMethod.valueOf(it)
            }
            val cname = rs.getString("cname")
            val ename = rs.getString("ename")
            val clogo = rs.getString("clogo")
            val elogo = rs.getString("elogo")

            val status = rs.getString("status").let{ Status.valueOf(it) }
            val sequence = rs.getInt("sequence")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            SlotGame(id = id, platform = platform, gameId = gameId, status = status, createdTime = createdTime, category = category,
                    hot = hot, new = new, cname = cname, ename = ename, clogo = clogo, elogo = elogo, launchs = launchs, sequence = sequence)
        }

    override fun findByPlatform(platform: Platform): List<SlotGame> {
        return query().where("platform", platform)
                .sort("sequence asc")
                .execute(mapper)
    }

    override fun create(slotGameCo: SlotGameValue.SlotGameCo): Boolean {
        return insert()
                .set("platform", slotGameCo.platform)
                .set("game_id", slotGameCo.gameId)
                .set("category", slotGameCo.category)
                .set("hot", slotGameCo.hot)
                .set("new", slotGameCo.new)
                .set("launchs", slotGameCo.launchs.joinToString(separator = ","))
                .set("cname", slotGameCo.cname)
                .set("ename", slotGameCo.ename)
                .set("clogo", slotGameCo.clogo)
                .set("elogo", slotGameCo.elogo)
                .set("sequence", slotGameCo.sequence)
                .set("status", slotGameCo.status)
                .executeOnlyOne()
    }

    override fun update(slotGameUo: SlotGameValue.SlotGameUo): Boolean {
        return update()
                .set("platform", slotGameUo.platform)
                .set("game_id", slotGameUo.gameId)
                .set("category", slotGameUo.category)
                .set("hot", slotGameUo.hot)
                .set("new", slotGameUo.new)
                .set("launchs", slotGameUo.launchs.joinToString(separator = ","))
                .set("cname", slotGameUo.cname)
                .set("ename", slotGameUo.ename)
                .set("clogo", slotGameUo.clogo)
                .set("elogo", slotGameUo.elogo)
                .set("sequence", slotGameUo.sequence)
                .set("status", slotGameUo.status)
                .where("id", slotGameUo.id)
                .executeOnlyOne()
    }
}