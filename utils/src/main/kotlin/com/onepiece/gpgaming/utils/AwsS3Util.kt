package com.onepiece.gpgaming.utils

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object AwsS3Util {

    private const val accessKey = "AKIA53BDZ52AVQFGNV5B"
    private const val secretKey = "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"
    private const val bucktName = "awspg1"
    private const val basePath = "https://s3.ap-southeast-1.amazonaws.com/$bucktName"
    private const val clientBasePath = "https://s3.ap-southeast-1.amazonaws.com"

    private val awsCreds = BasicAWSCredentials(accessKey, secretKey)
    private val s3Client = AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(awsCreds))
            .withRegion(Regions.AP_SOUTHEAST_1)
            .build()

    private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    private fun generatorFileName(category: String, scheme: String = ""): String {
        val now = LocalDateTime.now()
        val current = now.format(dateTimeFormat)
        val random = StringUtil.generateNumNonce(5)
        return "$category/$current$random${scheme}"
    }

    fun upload(file: File): String {

        val originFileName = file.name
        val scheme = originFileName.substring(originFileName.lastIndexOf("."))
        val randomFileName = generatorFileName(scheme)

        val putObjectRequest = PutObjectRequest(bucktName, randomFileName, file)
        putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead

        s3Client.putObject(putObjectRequest)
        return "$basePath/$randomFileName"
    }

    fun upload(file: MultipartFile, category: String): String {
        val originFileName = file.originalFilename!!
        val scheme = originFileName.substring(originFileName.lastIndexOf("."))
        val randomFileName = generatorFileName("client/$category", scheme)

        val putObjectRequest = PutObjectRequest(bucktName, randomFileName, file.inputStream, this.getObjectMetadata(scheme))
        putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead

        s3Client.putObject(putObjectRequest)
        return "$basePath/$randomFileName"
    }

    fun upload(file: MultipartFile, clientId: Int, category: String): String {
        val originFileName = file.originalFilename!!
        val scheme = originFileName.substring(originFileName.lastIndexOf("."))
        val randomFileName = generatorFileName("client/${clientId}/$category", scheme)

        val putObjectRequest = PutObjectRequest(bucktName, randomFileName, file.inputStream, this.getObjectMetadata(scheme))
        putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead

        s3Client.putObject(putObjectRequest)
        return "$basePath/$randomFileName"
    }

    fun clientUpload(file: MultipartFile, clientId: Int, path: String, profile: String): String {
        val originFileName = file.originalFilename!!
        val scheme = originFileName.substring(originFileName.lastIndexOf("."))
        val randomFileName = generatorFileName("client/${clientId}/$path", scheme)
        val bucktName = if (profile == "dev" || profile == "sit") "awspg1" else "awspg2"


        val putObjectRequest = PutObjectRequest(bucktName, randomFileName, file.inputStream, this.getObjectMetadata(scheme))
        putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead

        s3Client.putObject(putObjectRequest)

        return "$clientBasePath/$bucktName/$randomFileName"
    }

    fun uploadLocalFile(file: File, name: String, profile: String = "dev"): String {
        val bucktName = if (profile == "dev" || profile == "sit") "awspg1" else "awspg2"

        val putObjectRequest = PutObjectRequest(bucktName, name, file)
        putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead

        s3Client.putObject(putObjectRequest)
        return "$basePath/$name"
    }

    fun getObjectMetadata(scheme: String): ObjectMetadata {
        val objectMetadata = ObjectMetadata()
        when (scheme) {
            ".jpe" -> "image/jpeg"
            ".png" -> "image/png"
            ".gif" -> "image/gif"
            ".mp4" -> "video/mp4"
            else -> "application/octet-stream"
        }.apply {
            objectMetadata.contentType = this
        }

        return objectMetadata
    }


}

fun main() {
//    val fileList = File("/Users/cabbage/Desktop/logo/logo1225")
//
//    fileList.listFiles().map {  file ->
//        AwsS3Util.uploadLocalFile(file, "logo/${file.name}")
//    }
//
//    val fileList = File("/Users/cabbage/Downloads/live_re")
//
//    fileList.listFiles().map {  file ->
//        AwsS3Util.uploadLocalFile(file, "client/1/live/${file.name.replace(" ", "")}")
//    }

//    val fileList = File("/Users/cabbage/Downloads/apk")
//
//    fileList.listFiles().map {  file ->
//        AwsS3Util.uploadLocalFile(file, "apk/${file.name}")
//    }
//    val fileList = File("/Users/cabbage/Desktop/logo/logo")
//
//    fileList.listFiles().filter { it.name.contains(".jpeg") }.map {  file ->
//        AwsS3Util.uploadLocalFile(file, "origin_logo/${file.name}")
//    }
//
//    val fileList = File("/Users/cabbage/Desktop/origin_logo")
//
//    fileList.listFiles().map {  file ->
//        AwsS3Util.uploadLocalFile(file, "origin_logo/${file.name}")
//    }

//    val file = File("/Users/cabbage/Downloads/upcoming-matches-2-en.png")
//    val path = AwsS3Util.uploadLocalFile(file, "client/1/sport/s2.png")
//    println(path)

    val file = File("/Users/cabbage/Downloads/BANK 3")
    val list = file.listFiles().map { file ->
        val url = AwsS3Util.uploadLocalFile(file, "bank/logo/${file.name.replace(" ", "")}")
        file.name to url
    }

    list.forEach {
        println("${it.first} -- ${it.second}")
    }

}
