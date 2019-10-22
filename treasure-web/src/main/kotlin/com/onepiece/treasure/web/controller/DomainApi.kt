package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.value.DomainCo
import com.onepiece.treasure.web.controller.value.DomainUo
import com.onepiece.treasure.web.controller.value.DomainVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["domain"], description = " ")
interface DomainApi {

    @ApiOperation(tags = ["domain"], value = "all")
    fun all(): List<DomainVo>

    @ApiOperation(tags = ["domain"], value = "create")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody domainCo: DomainCo)

    @ApiOperation(tags = ["domain"], value = "update")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody domainUo: DomainUo)


}