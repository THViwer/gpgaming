package com.onepiece.treasure.web.controller

import com.onepiece.treasure.web.controller.basic.BasicController
import com.onepiece.treasure.web.controller.value.PlatformUo
import com.onepiece.treasure.web.controller.value.PlatformValueFactory
import com.onepiece.treasure.web.controller.value.PlatformVo
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