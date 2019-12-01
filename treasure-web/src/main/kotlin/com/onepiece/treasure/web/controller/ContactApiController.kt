package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.model.Contact
import com.onepiece.treasure.beans.value.internet.web.ContactValue
import com.onepiece.treasure.core.service.ContactService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

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
        contactService.create(clientId = getClientId(), type = create.type, number = create.number)
    }

    @PutMapping
    override fun update(@RequestBody update: ContactValue.Update) {
        contactService.update(id = update.id, number = update.number, status = update.status)
    }
}