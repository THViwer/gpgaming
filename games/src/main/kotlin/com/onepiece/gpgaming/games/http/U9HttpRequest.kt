package com.onepiece.gpgaming.games.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.onepiece.gpgaming.beans.enums.U9RequestStatus
import com.onepiece.gpgaming.games.bet.DEFAULT_DATETIMEFORMATTER
import com.onepiece.gpgaming.games.bet.JacksonMapUtil
import com.onepiece.gpgaming.games.bet.MapUtil
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

data class OKParam private constructor(

        // 线程号
        val nonce: String = UUID.randomUUID().toString(),

        // 请求地址
        val url: String,

        // 请求参数
        val param: String,

        // headers参数
        val headers: Map<String, String> = emptyMap(),

        // 表单请求参数
        val formParam: Map<String, Any> = emptyMap(),

        // 是否需要序列化成对象
        val serialization: Boolean = true,

        // 请求类型
        val mediaType: MediaType = U9HttpRequest.MEDIA_JSON,

        // 序列化类
        val clz: Class<out JacksonMapUtil> = JacksonMapUtil::class.java,

        // 请求方法
        val method: HttpMethod = HttpMethod.GET
) {

    companion object {

        fun ofGet(url: String, param: String, headers: Map<String, String> = emptyMap()): OKParam {
            return OKParam(url = url, param = param, headers = headers)
        }

        fun ofGetXml(url: String, param: String, headers: Map<String, String> = emptyMap()): OKParam {
            return OKParam(url = url, param = param, headers = headers, mediaType = U9HttpRequest.MEDIA_XML)
        }

        fun ofPost(url: String, param: String, headers: Map<String, String> = emptyMap(), formParam: Map<String, Any> = emptyMap()): OKParam {
            return OKParam(url = url, param = param, headers = headers, mediaType = U9HttpRequest.MEDIA_JSON, method = HttpMethod.POST, formParam = formParam)
        }

        fun ofPostXml(url: String, param: String, headers: Map<String, String> = emptyMap()): OKParam {
            return OKParam(url = url, param = param, headers = headers, mediaType = U9HttpRequest.MEDIA_XML, method = HttpMethod.POST)
        }

    }

}

data class GameResponse<T>(

        val okResponse: OKResponse,

        val data: T?
) {

    companion object {

        fun <T> of(data: T): GameResponse<T> {
            val okParam = OKParam.ofGet(url = "", param = "")

            val okResponse = OKResponse(url = "", method = HttpMethod.PATCH, param = "", response = "", message = "start demo game", eMapUtil = null, status = U9RequestStatus.OK,
                    okParam = okParam)
            return GameResponse(okResponse = okResponse, data = data)
        }

    }

}

data class OKResponse(

        // 请求参数
        val okParam: OKParam,

        // 请求地址
        val url: String,

        // 请求方式
        val method: HttpMethod,

        // 参数
        val param: String,

        // 响应参数
        val response: String,

        // 是否维护
        val maintain: Boolean = false,

        // 请求状态
        val status: U9RequestStatus,

        // 失败消息
        val message: String?,

        val eMapUtil: JacksonMapUtil?
) {

    // 请求是否成功
    val ok = status == U9RequestStatus.OK

    val mapUtil: MapUtil
        get() = eMapUtil!!.mapUtil


    fun asString(key: String): String {
        return this.mapUtil.asString(key = key)
    }

    fun asInt(key: String): Int {
        return mapUtil.asInt(key = key)
    }

    fun asBoolean(key: String): Boolean {
        return mapUtil.asBoolean(key = key)
    }

    fun asLong(key: String): Long {
        return mapUtil.asLong(key = key)
    }

    fun asBigDecimal(key: String): BigDecimal {
        return mapUtil.asBigDecimal(key = key)
    }

    fun asMap(key: String): MapUtil {
        return mapUtil.asMap(key = key)
    }

    fun asLocalDateTime(key: String, dateTimeFormatter: DateTimeFormatter = DEFAULT_DATETIMEFORMATTER): LocalDateTime {
        return mapUtil.asLocalDateTime(key = key, dateTimeFormatter = dateTimeFormatter)
    }

    fun asLocalDateTime(key: String): LocalDateTime {

        return mapUtil.asLocalDateTime(key = key)
    }

    fun asList(key: String): List<MapUtil> {
        return mapUtil.asList(key = key)
    }

}

//public inline fun <reified T> bindOKResponse(okParam: OKParam, response: Response, objectMapper: ObjectMapper): OKResponse<T> {
//
//
//    val body = response.body?.string() ?: "无法从{response.body}中获得返回"
//
//    val data = when (response.code) {
//        200, 201 -> {
//            objectMapper.readValue<T>(body)
//        }
//        else -> null
//    }
//
//    val ok = response.code == 200 || response.code == 201
//    return OKResponse(url = okParam.url, method = okParam.method, param = okParam.param, ok = ok, data = data)
//}


@Suppress("UNCHECKED_CAST")
@Component
class U9HttpRequest(
        private val objectMapper: ObjectMapper,
        private val xmlMapper: XmlMapper
) {

    private val log = LoggerFactory.getLogger(OkHttpUtil::class.java)

    companion object {
        val MEDIA_JSON = "application/json; charset=utf-8".toMediaType()
        val MEDIA_XML = "application/xml; charset=utf-8".toMediaType()
        val MEDIA_TEXT_XML = "text/xml; charset=utf-8".toMediaType()
        val MEDIA_TEXT = "text/html; charset=utf-8".toMediaType()
    }

    private val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS) //连接超时
            .readTimeout(20, TimeUnit.SECONDS) //读取超时
            .writeTimeout(20, TimeUnit.SECONDS) //写超时
            .build()

    private val httpsClient = OKHttpClientBuilder.buildOKHttpClient()
            .connectTimeout(20, TimeUnit.SECONDS) //连接超时
            .readTimeout(20, TimeUnit.SECONDS) //读取超时
            .writeTimeout(20, TimeUnit.SECONDS) //写超时
            .build()


    private fun getOkHttpClient(url: String): OkHttpClient {
        return if (url.startsWith("https://")) {
            httpsClient
        } else {
            client
        }
    }

    private fun bindOKResponse(okParam: OKParam, response: Response, objectMapper: ObjectMapper?): OKResponse {


        val body = response.body?.string() ?: "无法从{response.body}中获得返回"

        val mapUtil = try {
            when {
                !okParam.serialization -> null
                response.code == 200 || response.code == 201 -> {
                    objectMapper?.readValue(body, okParam.clz)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }

        val status = when (response.code) {
            200, 201 -> U9RequestStatus.OK
            else -> U9RequestStatus.Fail
        }
        return OKResponse(okParam = okParam, url = okParam.url, method = okParam.method, param = okParam.param, status = status, eMapUtil = mapUtil,
                response = body, message = response.message)
    }

    fun startRequest(okParam: OKParam, parseString: Boolean = false): OKResponse {

        val response = when {
            okParam.method == HttpMethod.GET -> this.get(param = okParam)
            okParam.formParam.isNotEmpty() -> this.postForm(okParam = okParam)
            okParam.method == HttpMethod.POST -> this.post(okParam = okParam)
            else -> error("未支持该请求方式")
        }

        val mapper = when {
            parseString -> null
            okParam.mediaType == MEDIA_JSON -> objectMapper
            okParam.mediaType == MEDIA_XML -> xmlMapper
            okParam.mediaType == MEDIA_TEXT_XML -> xmlMapper
            else -> objectMapper
        }
        return this.bindOKResponse(okParam = okParam, response = response, objectMapper = mapper)
    }

    private fun get(param: OKParam): Response {

        val url = "${param.url}?${param.param}"
        val request = Request.Builder().url(url).get()
        param.headers.forEach {
            request.addHeader(it.key, it.value)
        }
        return getOkHttpClient(param.url).newCall(request.build()).execute()
    }

    private fun post(okParam: OKParam): Response {

        val body = okParam.param.toRequestBody(okParam.mediaType)
        val builder = Request.Builder().url(okParam.url).post(body)

        okParam.headers.forEach {
            builder.addHeader(it.key, it.value)
        }

        val request = builder.build()
        return getOkHttpClient(okParam.url).newCall(request).execute()
    }

    private fun postForm(okParam: OKParam): Response {

        val body = FormBody.Builder()
        okParam.formParam.forEach { body.add(it.key, "${it.value}") }

        val request = Request.Builder().url(okParam.url).post(body.build())

        okParam.headers.forEach {
            request.addHeader(it.key, it.value)
        }
        return getOkHttpClient(okParam.url).newCall(request.build()).execute()
    }


}