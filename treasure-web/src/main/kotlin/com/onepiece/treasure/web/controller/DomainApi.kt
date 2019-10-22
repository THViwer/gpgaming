package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.value.DomainCo
import com.onepiece.treasure.web.controller.value.DomainUo
import com.onepiece.treasure.web.controller.value.DomainVo
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@Api(tags = ["setting"], description = " ")
interface DomainApi {

    @ApiOperation(tags = ["setting"], value = "domain -> all")
    fun all(): List<DomainVo>

    @ApiOperation(tags = ["setting"], value = "domain -> create")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun create(@RequestBody domainCo: DomainCo)

    @ApiOperation(tags = ["setting"], value = "domain -> update")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun update(@RequestBody domainUo: DomainUo)


}