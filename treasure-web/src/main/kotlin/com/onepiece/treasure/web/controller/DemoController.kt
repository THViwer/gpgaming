package com.onepiece.treasure.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/demo")
class DemoController {

    @GetMapping
    fun index(): String {

        return "success"
    }



}