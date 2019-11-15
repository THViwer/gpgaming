package com.onepiece.treasure.games.live.sexy

import com.onepiece.treasure.beans.model.token.SexyClientToken
import com.onepiece.treasure.games.GameConstant
import java.net.URLEncoder

class SexyBuild private constructor(val method: String){

    private val data = hashMapOf<String, Any>()

    companion object {
        fun instance(method: String): SexyBuild {
            return SexyBuild(method = method)
        }
    }

    fun set(k: String, v: Any?): SexyBuild {
        if (v != null)
            data[k] = v
        return this
    }

    fun build(token: SexyClientToken): String{
        val param = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        return "${GameConstant.SEXY_API_URL}/api/${token.appId}$method?${param}"
    }



    val x  =    "{\"appId\": \"MSN9MY\", \"key\": \"Aaa11111\"}"

}