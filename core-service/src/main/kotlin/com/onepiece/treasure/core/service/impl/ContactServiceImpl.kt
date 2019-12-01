package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.ContactType
import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.Contact
import com.onepiece.treasure.core.dao.ContactDao
import com.onepiece.treasure.core.service.ContactService
import org.springframework.stereotype.Service

@Service
class ContactServiceImpl(
        private val contactDao: ContactDao
) : ContactService {

    override fun create(clientId: Int, type: ContactType, number: String) {
        val state = contactDao.create(clientId = clientId, type = type, number = number)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(id: Int, number: String, status: Status) {
        val state = contactDao.update(id = id, number = number, status = status)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun list(clientId: Int): List<Contact> {
        return contactDao.all(clientId = clientId)
    }
}