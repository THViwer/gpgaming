package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.DomainCo
import com.onepiece.treasure.web.controller.value.DomainUo
import com.onepiece.treasure.web.controller.value.DomainValueFactory
import com.onepiece.treasure.web.controller.value.DomainVo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/domain")
class DomainApiController: BasicController(), DomainApi {

    @GetMapping
    override fun all(): List<DomainVo> {
        return DomainValueFactory.generatorDomains()
    }

    @PostMapping
    override fun create(@RequestBody domainCo: DomainCo) {
    }

    @PutMapping
    override fun update(@RequestBody domainUo: DomainUo) {
    }
}