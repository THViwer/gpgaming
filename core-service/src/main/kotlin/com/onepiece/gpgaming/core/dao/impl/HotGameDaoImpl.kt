package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.HotGameType
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.HotGame
import com.onepiece.gpgaming.beans.value.database.HotGameValue
import com.onepiece.gpgaming.core.dao.HotGameDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class HotGameDaoImpl : HotGameDao, BasicDaoImpl<HotGame>("hot_game") {

    override val mapper: (rs: ResultSet) -> HotGame
        get() = { rs ->
            val id = rs.getInt("id")

            val clientId = rs.getInt("client_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val gameId = rs.getString("game_id")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val type = rs.getString("type").let { HotGameType.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            HotGame(id = id, clientId = clientId, platform = platform, gameId = gameId, status = status,
                    type = type, createdTime = createdTime)
        }

    override fun create(co: HotGameValue.HotGameCo): Boolean {

        return insert().set("client_id", co.clientId)
                .set("platform", co.platform)
                .set("game_id", co.gameId)
                .set("type", co.type)
                .set("status", Status.Normal)
                .executeOnlyOne()
    }

    override fun update(uo: HotGameValue.HotGameUo): Boolean {
        return update().set("game_id", uo.gameId)
                .set("status", uo.status)
                .where("id", uo.id)
                .executeOnlyOne()
    }
}