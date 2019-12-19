package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.value.internet.web.BankVo
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankCoReq
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankUoReq
import com.onepiece.gpgaming.beans.value.internet.web.ClientBankVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["cash"], description = " ")
interface ClientBankApi {

    @ApiOperation(tags = ["cash"], value = "银行列表")
    fun banks(): List<BankVo>

    @ApiOperation(tags = ["cash"], value = "厅主银行卡 -> 列表")
    fun all(): List<ClientBankVo>

    @ApiOperation(tags = ["cash"], value = "厅主银行卡 -> 创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody clientBankCoReq: ClientBankCoReq)

    @ApiOperation(tags = ["cash"], value = "厅主银行卡 -> 更新")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody clientBankUoReq: ClientBankUoReq)

}