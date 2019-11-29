package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.utils.AwsS3Util
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/file")
class FileApiController : BasicController(), FileApi {

    @PostMapping("/upload")
    override fun uploadProof(
            @RequestParam("category") category: String,
            @RequestParam("file") file: MultipartFile
    ): Map<String, String> {
        val clientId = getClientId()

        val categoryName = when (category) {
            "banner" -> "banner"
            "promotion" -> "promotion"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        val url = AwsS3Util.upload(file = file, clientId = clientId, category = categoryName)
        return mapOf(
                "path" to url
        )
    }
}