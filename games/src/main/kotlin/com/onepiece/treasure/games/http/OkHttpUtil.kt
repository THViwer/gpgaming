package com.onepiece.treasure.games.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.onepiece.treasure.beans.exceptions.LogicException
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Component


@Suppress("UNCHECKED_CAST")
@Component
class OkHttpUtil(
        private val objectMapper: ObjectMapper
)  {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    fun <T> doGet(url: String, urlParam: String, responseClass: Class<T>): T {

        val request = Request.Builder()
                .url("$url$urlParam")
                .build()

        val response = client.newCall(request).execute()
        val json = response.body?.bytes()

        return if (responseClass == String::class.java) {
            String(json!!) as T
        } else {
            objectMapper.readValue(json, responseClass)
        }
    }

    fun <T> doGet(url: String, urlParam: String, function: (code: Int, json: String?) -> T): T {

        val request = Request.Builder()
                .url("$url$urlParam")
                .build()

        val response = client.newCall(request).execute()
        val json = response.body?.bytes()

        return function(response.code, json?.let { String(it) })
    }


    fun <T> doGet(url: String, param: Map<String, String>? = null, responseClass: Class<T>): T {
        val urlParam = param?.map { "${it.key}=${it.value}" }?.joinToString(separator = "&")?.let { "?" }?: ""
        return doGet(url, urlParam, responseClass)
    }

    fun <T> doPost(url: String, param: Any, responseClass: Class<T>): T {
        val body = objectMapper.writeValueAsBytes(param)
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
        return client.newCall(request).execute().use { response ->
            val bytes = response.body!!.bytes()
            objectMapper.readValue(bytes, responseClass)
        }
    }

    fun doPost(url: String, urlParam: String, function: ((code: Int) -> String)? = null) {
        val request = Request.Builder()
                .url(url)
                .method("post", null)
                .build()
        client.newCall(request).execute().use { response ->
            if (response.code != 200 && response.code != 201) {
                val errorCode = if (function == null) {
                    OnePieceExceptionCode.PLATFORM_METHOD_FAIL
                } else {
                    function(response.code)
                }

                throw LogicException(errorCode)
            }
        }
    }

    fun <T> doPost(url: String, urlParam: String, responseClass: Class<T>, function: ((code: Int) -> String)? = null): T {
        val request = Request.Builder()
                .url(url)
                .method("post", null)
                .build()
        return client.newCall(request).execute().use { response ->
            when {
                response.code == 200 || response.code == 201 -> {
                    val bytes = response.body!!.bytes()
                    objectMapper.readValue(bytes, responseClass)
                }
                else -> {
                    val errorCode = if (function == null) {
                        OnePieceExceptionCode.PLATFORM_METHOD_FAIL
                    } else {
                        function(response.code)
                    }

                    throw LogicException(errorCode)
                }
            }
        }
    }

    fun <T> doPost(url: String, urlParam: String, function: (response: Response) -> T) : T {
        val request = Request.Builder()
                .url(url)
                .method("post", null)
                .build()
        return client.newCall(request).execute().use { response ->
            function(response)
        }
    }

    fun <T> doPost(url: String, urlParam: String, param: Any, responseClass: Class<T>, function: (code: Int, json: String) -> T) : T {
        val body = objectMapper.writeValueAsBytes(param)
        val requestBody = body.toRequestBody(JSON)

        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
        return client.newCall(request).execute().use { response ->
            function(response.code, String(response.body!!.bytes()))
        }
    }

}