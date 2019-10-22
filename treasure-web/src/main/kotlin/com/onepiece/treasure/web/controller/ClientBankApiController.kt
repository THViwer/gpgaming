package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.ClientBankCo
import com.onepiece.treasure.web.controller.value.ClientBankUo
import com.onepiece.treasure.web.controller.value.ClientBankValueFactory
import com.onepiece.treasure.web.controller.value.ClientBankVo
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