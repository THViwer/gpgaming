package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.DomainValueFactory
import com.onepiece.treasure.web.controller.value.WebSiteCo
import com.onepiece.treasure.web.controller.value.WebSiteUo
import com.onepiece.treasure.web.controller.value.WebSiteVo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/domain")
class WebSiteApiController: BasicController(), WebSiteApi {



    @GetMapping
    override fun all(): List<WebSiteVo> {
        return DomainValueFactory.generatorWebSites()
    }

    @PostMapping
    override fun create(@RequestBody webSiteCo: WebSiteCo) {
    }

    @PutMapping
    override fun update(@RequestBody webSiteUo: WebSiteUo) {
    }
}