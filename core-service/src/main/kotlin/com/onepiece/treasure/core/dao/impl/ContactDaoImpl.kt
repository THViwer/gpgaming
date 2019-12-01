package com.onepiece.treasure.core.dao.impl

import com.onepiece.treasure.beans.enums.ContactType
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.Contact
import com.onepiece.treasure.core.dao.ContactDao
import com.onepiece.treasure.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ContactDaoImpl : BasicDaoImpl<Contact> ("contact"), ContactDao {

    override val mapper: (rs: ResultSet) -> Contact
        get() = { rs ->

            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val type = rs.getString("type").let { ContactType.valueOf(it) }
            val number = rs.getString("number")
            val status = rs.getString("status").let { Status.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()

            Contact(id = id, clientId = clientId, type = type, number = number, status = status, createdTime = createdTime)

        }

    override fun create(clientId: Int, type: ContactType, number: String): Boolean {
        return insert().set("client_id", clientId)
                .set("type", type)
                .set("number", number)
                .set("status", Status.Normal)
                .executeOnlyOne()
    }

    override fun update(id: Int, number: String, status: Status): Boolean {
        return update()
                .set("number", number)
                .set("status", status)
                .where("id", id)
                .executeOnlyOne()
    }
}