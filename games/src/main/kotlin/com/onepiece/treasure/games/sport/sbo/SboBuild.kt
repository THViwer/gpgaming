//package com.onepiece.treasure.games.sport.sbo
//
//import com.onepiece.treasure.beans.model.token.DefaultClientToken
//import com.onepiece.treasure.games.GameConstant
//import org.apache.commons.codec.digest.DigestUtils
//
//
//class SboBuild private constructor() {
//
//    companion object {
//        fun instance(): SboBuild {
//            return SboBuild()
//        }
//    }
//
//    fun build(token: DefaultClientToken, param: String): String {
//        val sign = DigestUtils.md5Hex("$param/${token.key}")
//        return "${GameConstant.SBO_API_URL}/sportsfundservice/$param?access=${sign}"
//    }
//
//    fun buildAppend(token: DefaultClientToken, param: String): String {
//        val sign = DigestUtils.md5Hex("$param/${token.key}")
//        return "${GameConstant.SBO_API_URL}/sportsfundservice/$param&access=${sign}"
//    }
//
//
//
//}