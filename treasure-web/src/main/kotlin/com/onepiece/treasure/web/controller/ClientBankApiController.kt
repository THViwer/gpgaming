package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.beans.value.internet.web.ClientBankCo
import com.onepiece.treasure.beans.value.internet.web.ClientBankUo
import com.onepiece.treasure.beans.value.internet.web.ClientBankValueFactory
import com.onepiece.treasure.beans.value.internet.web.ClientBankVo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientBank")
class ClientBankApiController : BasicController(), ClientBankApi {

    @GetMapping
    override fun all(): List<ClientBankVo> {
        return ClientBankValueFactory.generatorClientBanks()
    }

    @PostMapping
    override fun create(@RequestBody clientBankCo: ClientBankCo) {

    }

    @PutMapping
    override fun update(@RequestBody clientBankUo: ClientBankUo) {
    }
}