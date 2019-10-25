package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.beans.value.internet.web.PlatformUo
import com.onepiece.treasure.beans.value.internet.web.PlatformValueFactory
import com.onepiece.treasure.beans.value.internet.web.PlatformVo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/platform")
class ClientPlatformApiController: BasicController(), ClientPlatformApi {

    @GetMapping
    override fun all(): List<PlatformVo> {
        return PlatformValueFactory.generatorPlatforms()
    }

    @PutMapping
    override fun update(@RequestBody platformUo: PlatformUo) {
    }
}