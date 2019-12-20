package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.model.Contact
import com.onepiece.gpgaming.beans.value.internet.web.ContactValue
import com.onepiece.gpgaming.core.service.ContactService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contactUs")
class ContactApiController(
        private val contactService: ContactService
) : BasicController(), ContactApi {

    @GetMapping
    override fun all(): List<Contact> {
        return contactService.list(clientId = getClientId())
    }

    @PostMapping
    override fun create(@RequestBody create: ContactValue.Create) {
        contactService.create(clientId = getClientId(), type = create.type, number = create.number, qrCode = create.qrCode)
    }

    @PutMapping
    override fun update(@RequestBody update: ContactValue.Update) {
        contactService.update(id = update.id, number = update.number, status = update.status, qrCode = update.qrCode)
    }
}