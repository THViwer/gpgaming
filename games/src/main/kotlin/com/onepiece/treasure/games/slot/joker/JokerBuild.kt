package com.onepiece.treasure.games.slot.joker

import com.onepiece.treasure.games.GameConstant
import com.onepiece.treasure.games.joker.JokerConstant
import com.onepiece.treasure.games.token.DefaultClientToken
import okhttp3.FormBody
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.HmacUtils
import java.net.URLEncoder


class JokerBuild private constructor(
        val method: String
){

    private val data = hashMapOf<String, String>()
    private val timestamp = System.currentTimeMillis() / 1000

    companion object {
        fun instance(method: String): JokerBuild {
            val builder = JokerBuild(method)
            builder.data["Method"] = method
            builder.data["Timestamp"] = "${builder.timestamp}"
            return builder
        }
    }

    fun set(k: String, v: String?): JokerBuild {
        if (v != null)
            data[k] = v
        return this
    }

    fun build(token: DefaultClientToken): Pair<String, FormBody> {
        val urlParam = data.map { "${it.key}=${it.value}" }.sorted().joinToString(separator = "&")

        val bytes = HmacUtils.getHmacSha1(token.key.toByteArray()).doFinal(urlParam.toByteArray())
        val sign = URLEncoder.encode(Base64.encodeBase64String(bytes), "utf-8")

        val urlParamCodec = "AppID=${token.appId}&Signature=${sign}"
        return "${GameConstant.JOKER_URL}?$urlParamCodec" to getFormBody()
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