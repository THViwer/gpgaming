package com.onepiece.treasure.controller

import com.onepiece.treasure.controller.basic.BasicController
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/open")
class OpenApiController : BasicController(), OpenApi {

    private val log = LoggerFactory.getLogger(OpenApiController::class.java)

    @RequestMapping("/gameplay", produces = ["application/json;charset=utf-8"])
    override fun gamePlayLogin(): String {

        val request = getRequest()
        val data = request.inputStream.readBytes().let { String(it) }


        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <resp>
             <error_code>0</error_code>
             <cust_id>10001</cust_id>
             <cust_name>Dummy</cust_name>
             <currency_code>IDR</currency_code>
             <language>en-us</language>
             <test_cust>false</test_cust>
             <country>US</country>
             <date_of_birth>29-09-1989</date_of_birth>
             <ip>1.1.1.1</ip>
            </resp>
        """.trimIndent()
    }
}