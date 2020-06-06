package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.model.Contact

interface ContactService {

    fun create(clientId: Int, type: ContactType, role: Role, number: String, qrCode: String?)

    fun update(id: Int, number: String, status: Status, qrCode: String?)

    fun list(clientId: Int): List<Contact>

}