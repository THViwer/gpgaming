package com.onepiece.treasure.games.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.onepiece.treasure.beans.exceptions.LogicException
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
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

    private val client = OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.SECONDS) //连接超时
            .readTimeout(5000, TimeUnit.SECONDS) //读取超时
            .writeTimeout(5000, TimeUnit.SECONDS) //写超时
            .build()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val XML = "text/html; charset=utf-8".toMediaType()


    fun <T> doGet(url: String, clz: Class<T>): T {
        log.info("request url: $url")
        val request = Request.Builder()
                .url(url)
                .get()
                .build()

        val response = client.newCall(request).execute()
        check(response.code == 200) { OnePieceExceptionCode.PLATFORM_METHOD_FAIL }

        val json = response.body!!.string()
        log.info("response data: $json")

        if (clz == String::class.java)
            return json as T

        return objectMapper.readValue(json, clz)
    }


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
                    log.info("request url : $url")
                    log.info("result json : ${String(json)}")
                    objectMapper.readValue(json, clz)
                }
            }
        } else {
            throw LogicException(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }
    }

    fun <T> doPostJson(url: String, data: Any, clz: Class<T>): T {

        val json = if (data is String) {
            data
        } else {
            objectMapper.writeValueAsString(data)
        }

        log.info("request url : $url")
        log.info("request param: $json")

        val body = json.toRequestBody(JSON)

        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        val response = client.newCall(request).execute()
        if (response.code != 200) {
            log.error("$response")
            error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }

        val responseData = response.body!!.string()
        log.info("response json data : $responseData")
        return objectMapper.readValue(responseData, clz)
    }

    fun <T> doPostXml(url: String, data: String, clz: Class<T>): T {

        log.info("request url : $url")
        log.info("request param: $data")

        val body = data.toRequestBody(XML)

        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        val response = client.newCall(request).execute()
        if (response.code != 200) {
            log.error("$response")
            error(OnePieceExceptionCode.PLATFORM_METHOD_FAIL)
        }

        val responseData = response.body!!.string()
        log.info("response json data : $responseData")

        return xmlMapper.readValue(responseData, clz)
    }

}