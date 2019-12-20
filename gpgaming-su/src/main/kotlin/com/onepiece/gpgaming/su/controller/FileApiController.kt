package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.utils.AwsS3Util
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/file")
class FileApiController: FileApi {

    @PostMapping("/upload")
    override fun uploadProof(
            @RequestParam("clientId") clientId: Int,
            @RequestParam("category") category: String,
            @RequestParam("file") file: MultipartFile
    ): Map<String, String> {

        val categoryName = when (category) {
            "banner" -> "banner"
            "promotion" -> "promotion"
            "contact" -> "contact"
            "main_logo" -> "main_logo"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }

        val url = AwsS3Util.upload(file = file, clientId = clientId, category = categoryName)
        return mapOf(
                "path" to url
        )
    }
}