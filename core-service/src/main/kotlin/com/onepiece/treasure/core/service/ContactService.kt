package com.onepiece.treasure.core.service

import com.onepiece.treasure.beans.enums.ContactType
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.model.Contact

interface ContactService {

    fun create(clientId: Int, type: ContactType, number: String)

    fun update(id: Int, number: String, status: Status)

    fun list(clientId: Int): List<Contact>

}