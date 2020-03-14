package com.onepiece.gpgaming.games.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.exceptions.LogicException
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
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
        val TEXT_XML = "text/xml; charset=utf-8".toMediaType()
        val TEXT = "text/html; charset=utf-8".toMediaType()
    }

    val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) //连接超时
            .readTimeout(30, TimeUnit.SECONDS) //读取超时
            .writeTimeout(30, TimeUnit.SECONDS) //写超时
            .build()

    val httpsClient = OKHttpClientBuilder.buildOKHttpClient()
            .connectTimeout(30, TimeUnit.SECONDS) //连接超时
            .readTimeout(30, TimeUnit.SECONDS) //读取超时
            .writeTimeout(30, TimeUnit.SECONDS) //写超时
            .build()


    fun getOkHttpClient(url: String): OkHttpClient {
        return if (url.startsWith("https://")) {
            httpsClient
        } else {
            client
        }
    }

    fun <T> doGet(platform: Platform, url: String, clz: Class<T>,  headers: Map<String, String> = emptyMap()): T {
        val id = UUID.randomUUID().toString()
        log.info("okHttp post request, platform: $platform, requestId = $id, url = $url, headers = $headers")

        val request = Request.Builder()
                .url(url)
                .get()
        if (headers.isNotEmpty()) {
            headers.forEach {
                request.addHeader(it.key, it.value)
            }
        }

        val response = getOkHttpClient(url).newCall(request.build()).execute()
        check(response.code == 200) {
            val message = response.body?.string()
            log.error("请求失败：$message")
            OnePieceExceptionCode.PLATFORM_METHOD_FAIL
        }

        val json = response.body!!.string()
        log.info("okHttp post request, platform: $platform, requestId = $id, response = $json")

        if (clz == String::class.java)
            return json as T

        return objectMapper.readValue(json, clz)
    }

    fun <T> doGetXml(platform: Platform, url: String, clz: Class<T>, headers: Map<String, String> = emptyMap()): T {

        val id = UUID.randomUUID().toString()
        log.info("okHttp post request, platform: $platform, requestId = $id, url = $url, headers = $headers")

        val builder = Request.Builder().url(url)

        headers.map {
            builder.addHeader(it.key, it.value)
        }
//                .addHeader("Authorization", authorization)
        val request = builder.get().build()

        val response = getOkHttpClient(url).newCall(request).execute()
        check(response.code == 200) {
            val message = response.body?.string()
            log.error("请求失败：$message")
            OnePieceExceptionCode.PLATFORM_METHOD_FAIL
        }

        val json = response.body!!.string()
        log.info("okHttp post request, platform: $platform, requestId = $id, response = $json")

        if (clz == String::class.java)
            return json as T

        return xmlMapper.readValue(json, clz)
    }


    fun doPostForm(platform: Platform, url: String, body: FormBody, headers: Map<String, String> = emptyMap()){
        doPostForm(platform, url, body, String::class.java, headers) { code, response ->
            OnePieceExceptionCode.PLATFORM_METHOD_FAIL
        }
    }

    fun <T> doPostForm(platform: Platform, url: String, body: FormBody, clz: Class<T>, headers: Map<String, String> = emptyMap()): T {
        return doPostForm(platform = platform, url = url, body = body, clz = clz, headers = headers) { code, response ->
            error (OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }
    }

    fun <T> doPostForm(platform: Platform, url: String, body: FormBody, clz: Class<T>? = null, headers: Map<String, String> = emptyMap(), function: (code: Int, response: Response) -> T): T {

        val id = UUID.randomUUID().toString()
        log.info("okHttp post request,platform: $platform,  requestId = $id, url = $url, data = $body, headers = $headers")

        val request = Request.Builder()
                .url(url)
                .post(body)

        if (headers.isNotEmpty()) {
            headers.forEach {
                request.addHeader(it.key, it.value)
            }
        }

        val response = getOkHttpClient(url).newCall(request.build()).execute()

        val code = response.code
        if (code != 200 && code != 201) {
            log.error("请求错误：${response.body?.string()}")
            error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)

        }

        return when (clz) {
            null -> function(code, response)
            String::class.java -> {
                String(response.body!!.bytes()) as T
            }
            else -> {
                val json = response.body!!.bytes()
                log.info("okHttp post request, platform: $platform, requestId = $id, response = ${String(json)}")

                objectMapper.readValue(json, clz)
            }
        }

    }

    fun <T> doPostJson(platform: Platform, url: String, data: Any, clz: Class<T>): T {
        return this.doPostJson(platform, url, data, emptyMap(), clz)
    }

    fun <T> doPostJson(platform: Platform, url: String, data: Any, headers: Map<String, String>, clz: Class<T>): T {

        val json = if (data is String) {
            data
        } else {
            objectMapper.writeValueAsString(data)
        }

        val id = UUID.randomUUID().toString()
        log.info("okHttp post request, platform: $platform, requestId = $id, url = $url, data = $data, headers = $headers")
//        log.info("request url : $url")
//        log.info("request param: $json")

        val body = json.toRequestBody(JSON)

        val builder = Request.Builder()
                .url(url)
                .post(body)

        headers.forEach {
            builder.addHeader(it.key, it.value)
        }

        val request = builder.build()
        val response = getOkHttpClient(url).newCall(request).execute()
        if (response.code != 200) {
            val message = response.body?.string()
            log.error("post error: $message")
            error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }

        val responseData = response.body!!.string()

        log.info("okHttp post request, platform: $platform, requestId = $id, response = $responseData")
//        log.info("response json data : $responseData")
        return objectMapper.readValue(responseData, clz)
    }

    fun <T> doPostXml(platform: Platform, url: String, data: String, clz: Class<T>, mediaType: MediaType = XML, headers: Map<String, String> = emptyMap()): T {

        val id = UUID.randomUUID().toString()
        log.info("okHttp post request, platform: $platform, requestId = $id, url = $url, data = $data, headers = $headers")

        val body = data.toRequestBody(mediaType)

        val request = Request.Builder()
                .url(url)
                .post(body)

        if (headers.isNotEmpty()) {
            headers.forEach {
                request.addHeader(it.key, it.value)
            }
        }

        val response = getOkHttpClient(url).newCall(request.build()).execute()
        if (response.code != 200 && response.code != 201) {
            log.error("${response.body?.string()}")
            error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }

        val responseData = response.body!!.string()
        log.info("okHttp post request, platform: $platform, requestId = $id, response = $responseData")

        return if (clz != String::class.java) {
            xmlMapper.readValue(responseData, clz)
        } else {
            responseData as T
        }
    }

}