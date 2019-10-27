package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.WalletEvent
import com.onepiece.treasure.beans.model.WalletNote
import com.onepiece.treasure.beans.value.database.WalletNoteCo
import com.onepiece.treasure.beans.value.database.WalletNoteQuery
import com.onepiece.treasure.core.dao.WalletNoteDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class WalletNoteDaoImpl : BasicDaoImpl<WalletNote>("wallet_note"), WalletNoteDao {

    override val mapper: (rs: ResultSet) -> WalletNote
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val waiterId = rs.getInt("waiter_id")
            val memberId = rs.getInt("member_id")
            val eventId = rs.getString("event_id")
            val event = rs.getString("event").let { WalletEvent.valueOf(it) }
            val remarks = rs.getString("remarks")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            WalletNote(id = id, clientId = clientId, memberId = memberId, event = event, remarks = remarks, createdTime = createdTime,
                    waiterId = waiterId, eventId = eventId)
        }


    override fun query(walletNoteQuery: WalletNoteQuery): List<WalletNote> {
        return query().where("client_id", walletNoteQuery.clientId)
                .where("member_id", walletNoteQuery.memberId)
                .where("event", walletNoteQuery.event)
                .execute(mapper)
    }

    override fun create(walletNoteCo: WalletNoteCo): Boolean {
        return insert().set("client_id", walletNoteCo.clientId)
                .set("waiter_id", walletNoteCo.waiterId)
                .set("member_id", walletNoteCo.memberId)
                .set("event_id", walletNoteCo.eventId)
                .set("event", walletNoteCo.event)
                .set("remarks", walletNoteCo.remarks)
                .executeOnlyOne()
    }
}