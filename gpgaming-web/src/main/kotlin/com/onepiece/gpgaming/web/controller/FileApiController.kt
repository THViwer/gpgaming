package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.FileCategory
import com.onepiece.gpgaming.core.ActiveConfig
import com.onepiece.gpgaming.utils.AwsS3Util
import com.onepiece.gpgaming.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/file")
class FileApiController(
        private val activeConfig: ActiveConfig
) : BasicController(), FileApi {

    @PostMapping("/upload")
    override fun uploadProof(
            @RequestParam("category") category: FileCategory,
            @RequestParam("file") file: MultipartFile
    ): Map<String, String> {
        val clientId = getClientId()

//        val path = category.path
//                .let {
//                    SystemConstant.getClientResourcePath(clientId = clientId, profile = activeConfig.profile, defaultPath = it)
//
//                }
        val url = AwsS3Util.clientUpload(file = file, clientId = clientId, path = category.path, profile = activeConfig.profile)
        return mapOf(
                "path" to url
        )
    }
}