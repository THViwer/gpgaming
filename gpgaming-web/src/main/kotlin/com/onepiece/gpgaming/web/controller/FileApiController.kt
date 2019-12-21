package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.SystemConstant
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.games.ActiveConfig
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
            @RequestParam("category") category: String,
            @RequestParam("file") file: MultipartFile
    ): Map<String, String> {
        val clientId = getClientId()

        val path = when (category) {
            "promotion" -> "promotion"
            "banner" -> "banner"
            "Banner" -> "banner"
            "contact" -> "contact"
            "IndexLive" -> "live"
            "IndexSport" -> "sport"
            "IndexVideo" -> "video"
//            FileCategory.Banner -> "banner"
//            FileCategory.IndexLive -> "live"
//            FileCategory.IndexSport -> "sport"
//            FileCategory.IndexVideo -> "video"
            else -> error(OnePieceExceptionCode.DATA_FAIL)
        }.let {
            SystemConstant.getClientResourcePath(clientId = clientId, profile = activeConfig.profile, defaultPath = it)

        }
        val url = AwsS3Util.clientUpload(file = file, clientId = clientId, path = path, profile = activeConfig.profile)
        return mapOf(
                "path" to url
        )
    }
}