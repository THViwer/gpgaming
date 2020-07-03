package com.onepiece.gpgaming.player.controller.basic

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import springfox.documentation.annotations.ApiIgnore

@Controller
class IndexController {

    @GetMapping("/")
    @ApiIgnore
    fun index(): String? {
        return "redirect:swagger-ui.html"
    }
}