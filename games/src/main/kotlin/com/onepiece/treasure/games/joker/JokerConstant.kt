package com.onepiece.treasure.games.joker

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.treasure.games.http.OkHttpUtil
import com.onepiece.treasure.games.joker.value.JokerSlotGame
import com.onepiece.treasure.games.joker.value.JokerSlotGameResult
import okhttp3.FormBody
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.HmacUtils
import org.apache.commons.codec.net.URLCodec
import java.net.URLEncoder

object JokerConstant {

    val url = "http://api688.net:81"
//    val gameUrl = "http://www.gwc688.net"
    //TODO 做测试
    val gameUrl = "http://94.237.64.70/iframe.html"
//    const val url = "http://94.237.64.70:81"

    const val appId = "F1S8"

}

class JokerParamBuilder private constructor(
        val method: String
){

    private val data = hashMapOf<String, String>()
    private val timestamp = System.currentTimeMillis() / 1000

    companion object {
        fun instance(method: String): JokerParamBuilder {
            val builder = JokerParamBuilder(method)
            builder.data["Method"] = method
            builder.data["Timestamp"] = "${builder.timestamp}"
            return builder
        }
    }

    fun set(k: String, v: String): JokerParamBuilder {
        data[k] = v
        return this
    }

    fun build(): Pair<String, FormBody> {
        val urlParam = data.map { "${it.key}=${it.value}" }.sorted().joinToString(separator = "&")

        val bytes = HmacUtils.getHmacSha1("qc8y6kbyinc14".toByteArray()).doFinal(urlParam.toByteArray())
        val sign = URLEncoder.encode(Base64.encodeBase64String(bytes), "utf-8")

        val urlParamCodec = "AppID=${JokerConstant.appId}&Signature=${sign}"
        return "${JokerConstant.url}?$urlParamCodec" to getFormBody()
    }

    fun getFormBody(): FormBody {
        val builder = FormBody.Builder()
//                .add("Method", method)
//                .add("Timestamp", "$timestamp")
        data.map {
            builder.add(it.key, it.value)
        }
        return builder.build()
    }

}


fun main() {

    val mapper = jacksonObjectMapper()
    val okHttpUtil = OkHttpUtil(mapper)
//
//    val url = "http://api688.net:81"
//
//    val client = OkHttpClient()
//
//
//    val timestamp = System.currentTimeMillis() / 1000
//    val body = FormBody.Builder()
//            .add("Method", "ListGames")
//            .add("Timestamp", "$timestamp")
//            .build()
//
//    val signParam = "Method=ListGames&Timestamp=$timestamp"
//    val bytes = HmacUtils.getHmacSha1("qc8y6kbyinc14".toByteArray()).doFinal(signParam.toByteArray())
//    val sign = Base64.encodeBase64String(bytes)
//
//    val request = Request.Builder()
//            .url("$url?AppID=F1S8&Signature=$sign")
//            .post(body)
//            .build()
//    val response = client.newCall(request).execute()
//    println(response)
//    println(String(response.body!!.bytes()))

    val type = object: TypeReference<List<JokerSlotGame>>(){}

    val (url, formBody) = JokerParamBuilder.instance("ListGames").build()

    val data = okHttpUtil.doPostForm(url, formBody, JokerSlotGameResult::class.java)
    println(data)

}