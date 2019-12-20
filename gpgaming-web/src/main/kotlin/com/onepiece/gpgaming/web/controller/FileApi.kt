package com.onepiece.gpgaming.web.controller

import com.onepiece.gpgaming.beans.enums.FileCategory
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Api(tags = ["index"], description = " ")
interface FileApi {

    @ApiOperation(tags = ["index"], value = "上传图片")
    fun uploadProof(
            @RequestParam("category") category: FileCategory,
            @RequestParam("file") file: MultipartFile): Map<String, String>


}