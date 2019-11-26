//package com.onepiece.treasure.games.slot.mega
//
//import com.onepiece.treasure.beans.model.token.MegaClientToken
//import com.onepiece.treasure.games.GameConstant
//import org.apache.commons.codec.digest.DigestUtils
//import java.util.*
//
//class MegaBuild(
//) {
//    private val param = hashMapOf<String, Any>()
//
//    companion object {
//        fun instance(): MegaBuild {
//            return MegaBuild()
//        }
//
//    }
//
//    fun set(key: String, value: Any): MegaBuild {
//        param[key] = value
//        return this
//    }
//
//    private fun sign(token: MegaClientToken, random: String): String {
//
//        val loginId = param["loginId"]?.toString()?: ""
//        val amount = param["amount"]?.toString() ?: ""
//
//        val signParam = "$random${token.appId}${loginId}${amount}${token.key}"
//        return DigestUtils.md5Hex(signParam)
//    }
//
//
//
//    fun build(token: MegaClientToken, method: String): Pair<String, Any>{
//
//        val random = UUID.randomUUID().toString()
//        // postData.put("id", id);
//        //postData.put("method", method);
//        //postData.put("params", params);
//        //postData.put("jsonrpc", "2.0");
//
//        val sign = this.sign(token = token, random = random)
//
//        param["random"] = random
//        param["sn"] = token.appId
//        param["digest"] = sign
//
//        val map = hashMapOf<String, Any>()
//        map["id"] = random
//        map["sn"] = token.appId
//        map["params"] = param
//        map["method"] = method
//        map["jsonrpc"] = "2.0"
//
//        return "${GameConstant.MEGA_API_URL}/mega-cloud/api/" to map
//    }
//}