package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.ClientBankCo
import com.onepiece.treasure.beans.value.internet.web.ClientBankUo
import com.onepiece.treasure.beans.value.internet.web.ClientBankVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["setting"], description = " ")
interface ClientBankApi {

    @ApiOperation(tags = ["setting"], value = "bank -> all")
    fun all(): List<ClientBankVo>

    @ApiOperation(tags = ["setting"], value = "clientBank -> create")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody clientBankCo: ClientBankCo)

    @ApiOperation(tags = ["setting"], value = "clientBank -> update")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody clientBankUo: ClientBankUo)

}