package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.ShowPosition
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Contact
import com.onepiece.gpgaming.core.dao.ContactDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ContactDaoImpl : BasicDaoImpl<Contact>("contact"), ContactDao {

    override val mapper: (rs: ResultSet) -> Contact
        get() = { rs ->

            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val type = rs.getString("type").let { ContactType.valueOf(it) }
            val showPosition = rs.getString("show_position").let {
                ShowPosition.valueOf(it)
            }
            val number = rs.getString("number")
            val qrCode = rs.getString("qr_code")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            Contact(id = id, clientId = clientId, type = type, number = number, status = status, createdTime = createdTime, qrCode = qrCode,
                    showPosition = showPosition)
        }

    override fun create(clientId: Int, type: ContactType, showPosition: ShowPosition, number: String, qrCode: String?): Boolean {
        return insert().set("client_id", clientId)
                .set("type", type)
                .set("show_position", showPosition)
                .set("number", number)
                .set("qr_code", qrCode)
                .set("status", Status.Normal)
                .executeOnlyOne()
    }

    override fun update(id: Int, number: String, status: Status, showPosition: ShowPosition, qrCode: String?): Boolean {
        return update()
                .set("number", number)
                .set("qr_code", qrCode)
                .set("show_position", showPosition)
                .set("status", status)
                .where("id", id)
                .executeOnlyOne()
    }
}