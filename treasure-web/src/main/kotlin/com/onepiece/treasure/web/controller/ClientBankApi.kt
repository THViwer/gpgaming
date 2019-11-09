package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.value.internet.web.ClientBankCoReq
import com.onepiece.treasure.beans.value.internet.web.ClientBankUoReq
import com.onepiece.treasure.beans.value.internet.web.ClientBankVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["setting"], description = " ")
interface ClientBankApi {

    @ApiOperation(tags = ["setting"], value = "厅主银行卡 -> 列表")
    fun all(): List<ClientBankVo>

    @ApiOperation(tags = ["setting"], value = "厅主银行卡 -> 创建")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody clientBankCoReq: ClientBankCoReq)

    @ApiOperation(tags = ["setting"], value = "厅主银行卡 -> 更新")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody clientBankUoReq: ClientBankUoReq)

}