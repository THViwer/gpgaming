package com.onepiece.treasure.utils

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

    private const val accessKey = "AKIAIZ5L767H2KC5WYPA"
    private const val secretKey = "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"
    private const val bucktName = "awspg1"
    private const val basePath = "https://s3.ap-southeast-1.amazonaws.com/$bucktName"

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

    fun upload(file: MultipartFile, clientId: Int, category: String): String {
        val originFileName = file.originalFilename!!
        val scheme = originFileName.substring(originFileName.lastIndexOf("."))
        val randomFileName = generatorFileName("$category/$clientId", scheme)

        val putObjectRequest = PutObjectRequest(bucktName, randomFileName, file.inputStream, ObjectMetadata())
        putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead

        s3Client.putObject(putObjectRequest)
        return "$basePath/$randomFileName"
    }

    fun uploadLocalFile(file: File, name: String): String {

        val putObjectRequest = PutObjectRequest(bucktName, name, file)
        putObjectRequest.cannedAcl = CannedAccessControlList.PublicRead

        s3Client.putObject(putObjectRequest)
        return "$basePath/$name"

    }
}

fun main() {
//    val fileList = File("/Users/cabbage/Desktop/logo/logo1225")
//
//    fileList.listFiles().map {  file ->
//        AwsS3Util.uploadLocalFile(file, "logo/${file.name}")
//    }

    val fileList = File("/Users/cabbage/Downloads/apk")

    fileList.listFiles().map {  file ->
        AwsS3Util.uploadLocalFile(file, "apk/${file.name}")
    }
}
