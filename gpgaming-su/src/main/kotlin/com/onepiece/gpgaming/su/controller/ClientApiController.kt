package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.enums.Country
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.model.WebSite
import com.onepiece.gpgaming.beans.value.database.ClientCo
import com.onepiece.gpgaming.beans.value.database.ClientUo
import com.onepiece.gpgaming.beans.value.database.WebSiteCo
import com.onepiece.gpgaming.beans.value.database.WebSiteUo
import com.onepiece.gpgaming.core.IndexUtil
import com.onepiece.gpgaming.core.service.ClientService
import com.onepiece.gpgaming.core.service.WebSiteService
import com.onepiece.gpgaming.su.controller.value.ClientSuValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/client")
open class ClientApiController(
        private val clientService: ClientService,
        private val webSiteService: WebSiteService,
        private val indexUtil: IndexUtil
) : ClientApi {

    @PostMapping
    override fun create(@RequestBody clientCoReq: ClientSuValue.ClientCoReq) {

        if (clientCoReq.bossId == -1) {
            check(clientCoReq.country == Country.Default)
        } else {
            check(clientCoReq.country != Country.Default)

            val clients = clientService.all()
            val has = clients.filter { it.bossId == clientCoReq.bossId }.firstOrNull { it.country == clientCoReq.country }

            check(has == null) { OnePieceExceptionCode.DATA_EXIST }
        }


        val clientCo = ClientCo(username = clientCoReq.username, password = clientCoReq.password, name = clientCoReq.name,
                logo = clientCoReq.logo, whitelists = clientCoReq.whitelists, shortcutLogo = clientCoReq.shortcutLogo,
                bossId = clientCoReq.bossId, country = clientCoReq.country)
        clientService.create(clientCo)
    }

    @PutMapping
    override fun update(@RequestBody clientUoReq: ClientSuValue.ClientUoReq) {
        val clientUo = ClientUo(id = clientUoReq.id, password = clientUoReq.password, status = clientUoReq.status,
                name = clientUoReq.name, logo = clientUoReq.logo, whitelists = clientUoReq.whitelists, shortcutLogo = clientUoReq.shortcutLogo)
        clientService.update(clientUo)

        indexUtil.generatorIndexPage(clientUoReq.id)
    }

    @GetMapping
    override fun list(): List<ClientSuValue.ClientVo> {
        return clientService.all().map {
            ClientSuValue.ClientVo(id = it.id, username = it.username, name = it.name, openNumber = 100, createdTime = it.createdTime,
                    logo = it.logo, status = it.status, whitelists = it.whitelists, shortcutLogo = it.shortcutLogo, bossId = it.bossId,
                    country = it.country)
        }
    }

    @GetMapping("/webSite")
    override fun domains(@RequestParam(value = "clientId", required = false) clientId: Int?): List<WebSite> {
        return webSiteService.all().filter { clientId == null || it.clientId == clientId }
    }

    @PostMapping("/webSite")
    override fun create(@RequestBody webSiteCo: WebSiteCo) {

        val client = clientService.get(webSiteCo.clientId)

        webSiteService.create(webSiteCo = webSiteCo.copy(bossId = client.bossId))
    }

    @PutMapping("/webSite")
    override fun update(@RequestBody webSiteUo: WebSiteUo) {
        webSiteService.update(webSiteUo = webSiteUo)
    }
}
