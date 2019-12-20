package com.onepiece.gpgaming.su.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Api(tags = ["index"], description = " ")
interface FileApi {

    @ApiOperation(tags = ["index"], value = "上传图片")
    fun uploadProof(
            @RequestParam("clientId") clientId: Int,
            @RequestParam("category") category: String,
            @RequestParam("file") file: MultipartFile): Map<String, String>


}