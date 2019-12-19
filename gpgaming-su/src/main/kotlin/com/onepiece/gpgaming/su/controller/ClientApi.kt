package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.su.controller.value.ClientSuValue
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["client"], description = " ")
interface ClientApi {

    @ApiOperation(tags = ["client"], value = "厅主 -> 创建")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@RequestBody clientCoReq: ClientSuValue.ClientCoReq)

    @ApiOperation(tags = ["client"], value = "厅主 -> 更新")
    fun update(@RequestBody clientUoReq: ClientSuValue.ClientUoReq)

    @ApiOperation(tags = ["client"], value = "厅主 -> 列表")
    fun list(): List<ClientSuValue.ClientVo>


}