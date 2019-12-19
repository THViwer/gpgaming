package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.Bank
import com.onepiece.gpgaming.beans.value.database.ClientBankCo
import com.onepiece.gpgaming.beans.value.database.ClientBankUo
import com.onepiece.gpgaming.beans.value.internet.web.BankVo
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankCoReq
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankUoReq
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankVo
import com.onepiece.gpgaming.core.service.ClientBankService
import com.onepiece.gpgaming.core.service.LevelService
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/clientBank")
class ClientBankApiController(
        private val clientBankService: ClientBankService,
        private val levelService: LevelService
) : BasicController(), ClientBankApi {

    @GetMapping("/default/bank")
    override fun banks(): List<BankVo> {
        return Bank.values().map {
            BankVo(bank = it, name = it.cname, logo = it.logo)
        }
    }

    @GetMapping
    override fun all(): List<ClientBankVo> {

        val clientId = getClientId()
        val levelMap = levelService.all(clientId).map { it.id to it.name }.toMap()

        return clientBankService.findClientBank(clientId).map {
            with(it) {
                val levelName = levelMap[it.levelId]

                ClientBankVo(id = id, bank = bank, bankName = bank.cname, name = name, bankCardNumber = bankCardNumber,
                        status = status, createdTime = createdTime, levelId = levelId, levelName = levelName, logo = bank.logo)
            }
        }
    }

    @PostMapping
    override fun create(@RequestBody clientBankCoReq: ClientBankCoReq) {
        val clientId = getClientId()
        val clientBankCo = ClientBankCo(clientId = clientId, bank = clientBankCoReq.bank, bankCardNumber = clientBankCoReq.bankCardNumber,
                name = clientBankCoReq.name, levelId = clientBankCoReq.levelId)
        clientBankService.create(clientBankCo)

    }

    @PutMapping
    override fun update(@RequestBody clientBankUoReq: ClientBankUoReq) {
        val clientBankUo = ClientBankUo(id = clientBankUoReq.id, bank = clientBankUoReq.bank, bankCardNumber = clientBankUoReq.bankCardNumber,
                status = clientBankUoReq.status, name = clientBankUoReq.name)
        clientBankService.update(clientBankUo)
    }
}