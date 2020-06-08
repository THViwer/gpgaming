package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.ContactType
import com.onepiece.gpgaming.beans.enums.Role
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.Contact
import com.onepiece.gpgaming.beans.value.internet.web.ContactValue
import com.onepiece.gpgaming.core.service.ContactService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contactUs")
class ContactApiController(
        private val contactService: ContactService
) : BasicController(), ContactApi {

    @GetMapping
    override fun all(@RequestParam("role", defaultValue = "Member") role: Role): List<Contact> {
        return contactService.list(clientId = getClientId()).filter { it.role == role }
    }

    @PostMapping
    override fun create(@RequestBody create: ContactValue.Create) {

        val clientId = getClientId()

        when (create.type) {
            ContactType.Facebook,
            ContactType.Instagram,
            ContactType.YouTuBe  -> {
                val  has = contactService.list(clientId).filter { it.status  != Status.Delete }.firstOrNull { it.type == create.type }
                check(has == null ) { OnePieceExceptionCode.DATA_FAIL }
            }
            else  -> {}
        }

        check(create.role == Role.Member || create.role == Role.Agent) { OnePieceExceptionCode.DATA_FAIL }
        contactService.create(clientId = getClientId(), type = create.type, number = create.number, qrCode = create.qrCode, role = create.role)
    }

    @PutMapping
    override fun update(@RequestBody update: ContactValue.Update) {
        contactService.update(id = update.id, number = update.number, status = update.status, qrCode = update.qrCode)
    }
}