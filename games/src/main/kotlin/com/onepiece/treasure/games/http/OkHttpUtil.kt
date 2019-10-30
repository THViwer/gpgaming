package com.onepiece.treasure.games.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.exceptions.LogicException
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


@Suppress("UNCHECKED_CAST")
@Component
class OkHttpUtil(
        private val objectMapper: ObjectMapper
)  {

    private val log = LoggerFactory.getLogger(OkHttpUtil::class.java)

    private val client = OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.SECONDS) //连接超时
            .readTimeout(5000, TimeUnit.SECONDS) //读取超时
            .writeTimeout(5000, TimeUnit.SECONDS) //写超时
            .build()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    fun doPostForm(url: String, body: FormBody){
        doPostForm(url, body, String::class.java) { code, response ->
            OnePieceExceptionCode.PLATFORM_METHOD_FAIL
        }
    }

    fun <T> doPostForm(url: String, body: FormBody, clz: Class<T>): T {
        return doPostForm(url, body, clz) { code, response ->
            throw LogicException(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }
    }

    fun <T> doPostForm(url: String, body: FormBody, clz: Class<T>? = null, function: (code: Int, response: Response) -> T): T {

        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

        val response = client.newCall(request).execute()

        //TODO code
        val code = response.code

        return if (code == 200 || code == 201) {

            when (clz) {
                null -> function(code, response)
                String::class.java -> {
                    String(response.body!!.bytes()) as T
                }
                else -> {
                    val json = response.body!!.bytes()
                    println(String(json))
                    objectMapper.readValue(json, clz)
                }
            }
        } else {
            println(response)
            throw LogicException(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }
    }

//    fun <T> doGet(url: String, urlParam: String, responseClass: Class<T>): T {
//
//        val request = Request.Builder()
//                .url("$url?$urlParam")
//                .build()
//
//        val response = client.newCall(request).execute()
//        val json = response.body?.bytes()
//
//        return if (responseClass == String::class.java) {
//            String(json!!) as T
//        } else {
//            objectMapper.readValue(json, responseClass)
//        }
//    }
//
//    fun <T> doGet(url: String, urlParam: String, function: (code: Int, json: String?) -> T): T {
//
//        val request = Request.Builder()
//                .url("$url$urlParam")
//                .build()
//
//        val response = client.newCall(request).execute()
//        val json = response.body?.bytes()
//
//        return function(response.code, json?.let { String(it) })
//    }
//
//
//    fun <T> doGet(url: String, param: Map<String, String>? = null, responseClass: Class<T>): T {
//        val urlParam = param?.map { "${it.key}=${it.value}" }?.joinToString(separator = "&")?.let { "?" }?: ""
//        return doGet(url, urlParam, responseClass)
//    }
//
//    fun <T> doPost(url: String, param: Any, responseClass: Class<T>): T {
//        val body = objectMapper.writeValueAsBytes(param)
//        val requestBody = body.toRequestBody(JSON)
//
//        val request = Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build()
//        return client.newCall(request).execute().use { response ->
//            val bytes = response.body!!.bytes()
//            objectMapper.readValue(bytes, responseClass)
//        }
//    }
//
//    fun doPost(url: String, urlParam: String, function: ((code: Int) -> String)? = null) {
//        val request = Request.Builder()
//                .url(url)
//                .method("post", null)
//                .build()
//        client.newCall(request).execute().use { response ->
//            if (response.code != 200 && response.code != 201) {
//                val errorCode = if (function == null) {
//                    OnePieceExceptionCode.PLATFORM_METHOD_FAIL
//                } else {
//                    function(response.code)
//                }
//
//                throw LogicException(errorCode)
//            }
//        }
//    }
//
//    fun <T> doPost(url: String, urlParam: String, responseClass: Class<T>, function: ((code: Int) -> String)? = null): T {
//
//        val body = RequestBody.create(null, byteArrayOf())
//
//        val request = Request.Builder()
//                .url(url)
//                .method("post", body)
//                .build()
//        return client.newCall(request).execute().use { response ->
//
//            log.warn("response = $response")
//            log.warn("code = ${response.code}")
//            log.warn("message = ${response.message}")
//            val msg = String(response.body?.bytes()?:"error".toByteArray())
//            log.warn("msg = $msg")
//            when {
//                response.code == 200 || response.code == 201 -> {
//                    val bytes = response.body!!.bytes()
//                    objectMapper.readValue(bytes, responseClass)
//                }
//                else -> {
//                    val errorCode = if (function == null) {
//                        OnePieceExceptionCode.PLATFORM_METHOD_FAIL
//                    } else {
//                        function(response.code)
//                    }
//
//                    throw LogicException(errorCode)
//                }
//            }
//        }
//    }
//
//    fun <T> doPost(url: String, urlParam: String, function: (response: Response) -> T) : T {
//        val request = Request.Builder()
//                .url(url)
//                .method("post", null)
//                .build()
//        return client.newCall(request).execute().use { response ->
//            function(response)
//        }
//    }
//
//    fun <T> doPost(url: String, urlParam: String, param: Any, responseClass: Class<T>, function: (code: Int, json: String) -> T) : T {
//        val body = objectMapper.writeValueAsBytes(param)
//        val requestBody = body.toRequestBody(JSON)
//
//        val request = Request.Builder()
//                .url(url)
//                .post(requestBody)
//                .build()
//        return client.newCall(request).execute().use { response ->
//            function(response.code, String(response.body!!.bytes()))
//        }
//    }

}