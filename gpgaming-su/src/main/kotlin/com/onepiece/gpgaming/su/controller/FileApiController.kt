package com.onepiece.gpgaming.su.controller

import com.onepiece.gpgaming.beans.enums.FileCategory
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
            @RequestParam("category") category: FileCategory,
            @RequestParam("file") file: MultipartFile
    ): Map<String, String> {

        val url = AwsS3Util.upload(file = file, category = category.path)
        return mapOf(
                "path" to url
        )
    }
}