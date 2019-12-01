package com.onepiece.treasure.core.dao

import com.onepiece.treasure.beans.enums.ContactType
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.Contact
import com.onepiece.treasure.core.dao.basic.BasicDao

interface ContactDao : BasicDao<Contact> {

    fun create(clientId: Int, type: ContactType, number: String): Boolean

    fun update(id: Int, number: String, status: Status): Boolean
}
