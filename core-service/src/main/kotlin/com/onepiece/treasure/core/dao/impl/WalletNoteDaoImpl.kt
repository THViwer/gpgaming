package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.core.dao.WalletNoteDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import com.onepiece.treasure.core.dao.value.WalletNoteCo
import com.onepiece.treasure.core.model.WalletNote
import com.onepiece.treasure.core.model.WalletQuery
import com.onepiece.treasure.core.model.enums.WalletEvent
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class WalletNoteDaoImpl : BasicDaoImpl<WalletNote>("wallet_note"), WalletNoteDao {

    override fun mapper(): (rs: ResultSet) -> WalletNote {
        return { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val memberId = rs.getInt("member_id")
            val event = rs.getString("event").let { WalletEvent.valueOf(it) }
            val remark = rs.getString("remark")
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            WalletNote(id = id, clientId = clientId, memberId = memberId, event = event, remark = remark, createdTime = createdTime)
        }
    }

    override fun query(walletQuery: WalletQuery): List<WalletNote> {
        return query().where("client_id", walletQuery.clientId)
                .where("member_id", walletQuery.memberId)
                .where("event", walletQuery.event)
                .execute(mapper())
    }

    override fun create(walletNoteCo: WalletNoteCo): Boolean {
        return insert().set("client_id", walletNoteCo.clientId)
                .set("member_id", walletNoteCo.memberId)
                .set("event", walletNoteCo.event)
                .set("remark", walletNoteCo.remark)
                .executeOnlyOne()
    }
}