package com.onepiece.treasure.games.joker

import okhttp3.FormBody
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.HmacUtils
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

    fun set(k: String, v: String?): JokerParamBuilder {
        if (v != null)
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

//
//fun main() {
//
//    val mapper = jacksonObjectMapper()
//    val okHttpUtil = OkHttpUtil(mapper)
////
////    val url = "http://api688.net:81"
////
////    val client = OkHttpClient()
////
////
////    val timestamp = System.currentTimeMillis() / 1000
////    val body = FormBody.Builder()
////            .add("Method", "ListGames")
////            .add("Timestamp", "$timestamp")
////            .build()
////
////    val signParam = "Method=ListGames&Timestamp=$timestamp"
////    val bytes = HmacUtils.getHmacSha1("qc8y6kbyinc14".toByteArray()).doFinal(signParam.toByteArray())
////    val sign = Base64.encodeBase64String(bytes)
////
////    val request = Request.Builder()
////            .url("$url?AppID=F1S8&Signature=$sign")
////            .post(body)
////            .build()
////    val response = client.newCall(request).execute()
////    println(response)
////    println(String(response.body!!.bytes()))
//
////    val type = object: TypeReference<List<JokerSlotGame>>(){}
//////
//////    val (url, formBody) = JokerParamBuilder.instance("ListGames").build()
//////
//////    val data = okHttpUtil.doPostForm(url, formBody, JokerSlotGameResult::class.java)
//////    println(data)
//
//
//    val endTime = LocalDateTime.now()
//    val startTime = LocalDateTime.now().minusHours(1)
//
//    // 2019-10-30T18:38:10
//    println("startTime = $startTime, endTime = $endTime")
//
//    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//
//
//    val (url, formBody) = JokerParamBuilder.instance("TS")
//            .set("StartDate", startTime.format(dateFormatter))
//            .set("EndDate", endTime.format(dateFormatter))
//            .set("NextId", UUID.randomUUID().toString().replace("-", ""))
//            .build()
//
//    val betResult = okHttpUtil.doPostForm(url, formBody, BetResult::class.java)
//    println(betResult)
//
//}