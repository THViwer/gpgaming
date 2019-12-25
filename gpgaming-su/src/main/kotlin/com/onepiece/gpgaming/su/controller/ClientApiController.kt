package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.value.database.ClientCo
import com.onepiece.gpgaming.beans.value.database.ClientUo
import com.onepiece.gpgaming.core.IndexUtil
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.su.controller.value.ClientSuValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/client")
open class ClientApiController(
        private val clientService: ClientService,
        private val indexUtil: IndexUtil
) : ClientApi {

    @PostMapping
    override fun create(@RequestBody clientCoReq: ClientSuValue.ClientCoReq) {
        val clientCo = ClientCo(username = clientCoReq.username, password = clientCoReq.password, name = clientCoReq.name,
                logo = clientCoReq.logo)
        clientService.create(clientCo)
    }

    @PutMapping
    override fun update(@RequestBody clientUoReq: ClientSuValue.ClientUoReq) {
        val clientUo = ClientUo(id = clientUoReq.id, password = clientUoReq.password, status = clientUoReq.status,
                name = clientUoReq.name, logo = clientUoReq.logo)
        clientService.update(clientUo)

        indexUtil.generatorIndexPage(clientUoReq.id)
    }

    @GetMapping
    override fun list(): List<ClientSuValue.ClientVo> {
        return clientService.all().map {
            //TODO 暂时100
            ClientSuValue.ClientVo(id = it.id, username = it.username, name = it.name, openNumber = 100, createdTime = it.createdTime,
                    logo = it.logo)
        }

    }
}
