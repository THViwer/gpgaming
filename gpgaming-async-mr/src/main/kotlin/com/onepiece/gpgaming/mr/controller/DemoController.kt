package com.onepiece.gpgaming.mr.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DemoController {

    @GetMapping("/test")
    fun test(): String {
        return "Hello"
    }
}