package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Contact
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface ContactDao : BasicDao<Contact> {

    fun create(clientId: Int, type: ContactType, role: Role, number: String, qrCode: String?): Boolean

    fun update(id: Int, number: String, status: Status, qrCode: String?): Boolean

}
