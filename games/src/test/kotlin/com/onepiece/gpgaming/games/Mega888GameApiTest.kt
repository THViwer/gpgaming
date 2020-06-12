//package com.onepiece.gpgaming.games
//
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.apache.commons.codec.digest.DigestUtils
//import org.junit.Test
//import java.util.*
//
//class Mega888GameApiTest : BaseTest() {
//
//
//    @Test
//    fun demo() {
//
////        val url = "http://94.237.64.70:82/api/signup/CT01060502/"
//        val url = "http://api.ctapi888.com/api/signup/CT01060502/"
//
//        // MD5(agentName+API key + 随机字符串)
//
//        val password = DigestUtils.md5Hex("123456")
//        val random = UUID.randomUUID().toString().replace("-", "")
//
//        val token = DigestUtils.md5Hex("CT010605026bd7291cd0ce4f808dde9b67f114cbb2$random")
//        val data = """
//                        {
//                            "token":"$token",
//                            "random":"$random",
//                            "data":"G",
//                            "member":{
//                                "username":"a001001",
//                                "password":"$password",
//                                "currencyName":"MYR",
//                                "winLimit":1000
//                            }
//                        }
//        """.trimIndent()
//
//        val okHttpClient = OkHttpClient()
//        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
//        val requestBody = data.toRequestBody(JSON)
//        val request = Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build()
//        val response = okHttpClient.newCall(request).execute()
//
//        println(response.code)
//        println(response.message)
//        println(response.body!!.string())
//    }
//
//
//    @Test
//    fun startGame() {
//        val url = "http://api.ctapi888.com/api/login/CT01060502/"
//        val password = DigestUtils.md5Hex("123456")
//
//        val random = UUID.randomUUID().toString().replace("-", "")
//        val token = DigestUtils.md5Hex("CT010605026bd7291cd0ce4f808dde9b67f114cbb2$random")
//
//        val data = """
//                        {
//                            "token":"$token",
//                            "random":"$random",
//                            "lang":"en",
//                            "member":{
//                                "username":"a001001",
//                                "password":"123456"
//                            }
//                        }
//        """.trimIndent()
//
//        val okHttpClient = OkHttpClient()
//        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
//        val requestBody = data.toRequestBody(JSON)
//        val request = Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build()
//        val response = okHttpClient.newCall(request).execute()
//
//        println(response.code)
//        println(response.message)
//        println(response.body!!.string())
//    }
//}