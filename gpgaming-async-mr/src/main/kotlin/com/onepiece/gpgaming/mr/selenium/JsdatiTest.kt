package com.onepiece.gpgaming.mr.selenium

import java.io.File

class JsdatiTest {

    val file = File("/Users/cabbage/Downloads/img_code_test.jpg")

    val uploadPath = "https://v2-api.jsdama.com/upload"


    val imgData = "data:image/png;base64,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"
    val daa = """
        {
            "softwareId": 100008,
            "softwareSecret": "HQVvQhd81CHcnhVp7aQ6Lc777vBNC6D16ASdcXCz",
            "username": "cabbage_cgh",
            "password": "Xiaobai520",
            "captchaData": "$imgData",
            "captchaType": 1,
            "captchaMinLength": 0,
            "captchaMaxLength": 0
        }
    """.trimIndent()

}