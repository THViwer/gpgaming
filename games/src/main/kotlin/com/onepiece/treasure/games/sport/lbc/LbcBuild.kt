package com.onepiece.treasure.games.sport.lbc

import com.onepiece.treasure.beans.model.token.DefaultClientToken
import com.onepiece.treasure.games.GameConstant
import org.apache.commons.codec.digest.DigestUtils

class LbcBuild private constructor(val method: String){

    private val data = hashMapOf<String, Any>()

    companion object {
        fun instance(method: String): LbcBuild {
            return LbcBuild(method = method)
        }
    }

    fun set(k: String, v: Any?): LbcBuild {
        if (v != null)
            data[k] = v
        return this
    }

    fun build(token: DefaultClientToken): String{
        val signParam = data.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
        val signPath = "${token.key}$method?$signParam"
        val sign = DigestUtils.md5Hex(signPath)
        return "${GameConstant.LBC_API_URL}$method?SecurityToken=$sign&$signParam"
    }


}