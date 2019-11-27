package com.onepiece.treasure.games.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.onepiece.treasure.beans.exceptions.LogicException
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit


@Suppress("UNCHECKED_CAST")
@Component
class OkHttpUtil(
        private val objectMapper: ObjectMapper,
        private val xmlMapper: XmlMapper
)  {

    private val log = LoggerFactory.getLogger(OkHttpUtil::class.java)

    companion object {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val XML = "application/xml; charset=utf-8".toMediaType()
        val TEXT = "text/html; charset=utf-8".toMediaType()
    }

    private val client = OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.SECONDS) //连接超时
            .readTimeout(5000, TimeUnit.SECONDS) //读取超时
            .writeTimeout(5000, TimeUnit.SECONDS) //写超时
            .build()


    fun <T> doGet(url: String, clz: Class<T>,  headers: Map<String, String> = emptyMap()): T {
        log.info("request url: $url")
        val request = Request.Builder()
                .url(url)
                .get()
        if (headers.isNotEmpty()) {
            headers.forEach {
                request.addHeader(it.key, it.value)
            }
        }

        val response = client.newCall(request.build()).execute()
        check(response.code == 200) {
            val message = response.body?.string()
            log.error("post error: ", message)
            OnePieceExceptionCode.PLATFORM_METHOD_FAIL
        }

        val json = response.body!!.string()
        log.info("response data: $json")

        if (clz == String::class.java)
            return json as T

        return objectMapper.readValue(json, clz)
    }

    fun <T> doGetXml(url: String, clz: Class<T>, authorization: String = ""): T {
        log.info("request url: $url")
        val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", authorization)
                .get()
                .build()

        val response = client.newCall(request).execute()
        check(response.code == 200) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }

        val json = response.body!!.string()
        log.info("response data: $json")

        if (clz == String::class.java)
            return json as T

        return xmlMapper.readValue(json, clz)
    }


    fun doPostForm(url: String, body: FormBody, headers: Map<String, String> = emptyMap()){
        doPostForm(url, body, String::class.java, headers) { code, response ->
            OnePieceExceptionCode.PLATFORM_METHOD_FAIL
        }
    }

    fun <T> doPostForm(url: String, body: FormBody, clz: Class<T>, headers: Map<String, String> = emptyMap()): T {
        return doPostForm(url = url, body = body, clz = clz, headers = headers) { code, response ->
            throw LogicException(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }
    }

    fun <T> doPostForm(url: String, body: FormBody, clz: Class<T>? = null, headers: Map<String, String> = emptyMap(), function: (code: Int, response: Response) -> T): T {

        val request = Request.Builder()
                .url(url)
                .post(body)

        if (headers.isNotEmpty()) {
            headers.forEach {
                request.addHeader(it.key, it.value)
            }
        }

        val response = client.newCall(request.build()).execute()

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
                    log.info("request url : $url")
                    log.info("result json : ${String(json)}")
                    objectMapper.readValue(json, clz)
                }
            }
        } else {
            error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }
    }

    fun <T> doPostJson(url: String, data: Any, clz: Class<T>): T {
        return this.doPostJson(url, data, emptyMap(), clz)
    }

    fun <T> doPostJson(url: String, data: Any, headers: Map<String, String>, clz: Class<T>): T {

        val json = if (data is String) {
            data
        } else {
            objectMapper.writeValueAsString(data)
        }

        log.info("request url : $url")
        log.info("request param: $json")

        val body = json.toRequestBody(JSON)

        val builder = Request.Builder()
                .url(url)
                .post(body)

        headers.forEach {
            builder.addHeader(it.key, it.value)
        }

        val request = builder.build()
        val response = client.newCall(request).execute()
        if (response.code != 200) {
            val message = response.body?.string()
            log.error("post error: ", message)
            error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }

        val responseData = response.body!!.string()
        log.info("response json data : $responseData")
        return objectMapper.readValue(responseData, clz)
    }

    fun <T> doPostXml(url: String, data: String, clz: Class<T>, mediaType: MediaType = XML, headers: Map<String, String> = emptyMap()): T {

        log.info("request url : $url")
        log.info("request param: $data")

        val body = data.toRequestBody(mediaType)

        val request = Request.Builder()
                .url(url)
                .post(body)

        if (headers.isNotEmpty()) {
            headers.forEach {
                request.addHeader(it.key, it.value)
            }
        }

        val response = client.newCall(request.build()).execute()
        if (response.code != 200 && response.code != 201) {
            log.error("$response")
            error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }

        val responseData = response.body!!.string()
        log.info("response json data : $responseData")

        return if (clz != String::class.java) {
            xmlMapper.readValue(responseData, clz)
        } else {
            responseData as T
        }
    }

}