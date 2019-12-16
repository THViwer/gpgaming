package com.onepiece.treasure.su.controller

import com.onepiece.treasure.beans.value.database.ClientCo
import com.onepiece.treasure.beans.value.database.ClientUo
import com.onepiece.treasure.core.service.ClientService
import com.onepiece.treasure.su.controller.value.ClientSuValue
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/client")
open class ClientApiController(
        private val clientService: ClientService
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
    }

    @GetMapping
    override fun list(): List<ClientSuValue.ClientVo> {
        return clientService.all().map {
            //TODO 暂时100
            ClientSuValue.ClientVo(id = it.id, username = it.username, name = it.name, openNumber = 100, createdTime = it.createdTime)
        }

    }
}
