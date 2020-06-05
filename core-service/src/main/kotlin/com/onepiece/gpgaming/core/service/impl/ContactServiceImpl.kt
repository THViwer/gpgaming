package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Contact
import com.onepiece.gpgaming.core.dao.ContactDao
import com.onepiece.gpgaming.core.service.ContactService
import org.springframework.stereotype.Service

@Service
class ContactServiceImpl(
        private val contactDao: ContactDao
) : ContactService {

    override fun create(clientId: Int, type: ContactType, role: Role, number: String, qrCode: String?) {
        val state = contactDao.create(clientId = clientId, type = type, role = role, number = number, qrCode = qrCode)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun update(id: Int, number: String, status: Status, qrCode: String?) {
        val state = contactDao.update(id = id, number = number, status = status, qrCode = qrCode)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }
    }

    override fun list(clientId: Int): List<Contact> {
        return contactDao.all(clientId = clientId)
    }
}