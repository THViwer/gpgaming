package com.onepiece.treasure.games.bet

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.StringUtils
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.IvParameterSpec


object DesUtil {

    private val NULL_IV = Base64.decodeBase64("AAAAAAAAAAA=")

    /**
     * 解密<br></br>
     * @param data 被解码的数据(注意编码转换)
     * @param key key
     * @param iv 向量(必须为8byte)
     * @return
     * @throws GeneralSecurityException
     */
    @Throws(GeneralSecurityException::class)
    fun decrypt(data: ByteArray?, key: ByteArray?, iv: ByteArray?): ByteArray? {
        val keyFactory = SecretKeyFactory.getInstance("DESede")
        val sec = keyFactory.generateSecret(DESedeKeySpec(key))
        val cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding")
        val IvParameters = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, sec, IvParameters)
        return cipher.doFinal(data)
    }

    /**
     * 加密<br></br>
     * @param data 被解码的数据(注意编码转换)
     * @param key key
     * @param iv 向量(必须为8byte)
     * @return
     * @throws GeneralSecurityException
     */
    @Throws(GeneralSecurityException::class)
    fun encrypt(data: ByteArray?, key: ByteArray?, iv: ByteArray?): ByteArray? {
        val keyFactory = SecretKeyFactory.getInstance("DESede")
        val sec = keyFactory.generateSecret(DESedeKeySpec(key))
        val cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding")
        val IvParameters = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, sec, IvParameters)
        return cipher.doFinal(data)
    }

    /**
     * 解密<br></br>
     * ps: 向量使用key的前8个byte
     * @param base64edData 经过base64编码的数据
     * @param base64edKey 经过base64编码的数据
     * @return 解密后的数据(UTF-8编码)
     * @param base64edIv 为空则使用全0
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    @Throws(GeneralSecurityException::class, UnsupportedEncodingException::class)
    fun decrypt(base64edData: String?, base64edKey: String?, base64edIv: String?): String? {
        val keyByte: ByteArray = Base64.decodeBase64(base64edKey)
        return StringUtils.newStringUtf8(decrypt(Base64.decodeBase64(base64edData), keyByte, if (base64edIv == null) NULL_IV else Base64.decodeBase64(base64edIv)))
    }

    /**
     * 加密<br></br>
     * ps: 向量使用key的前8个byte
     * @param data 被加密的数据
     * @param base64edKey 经过base64编码的数据
     * @param base64edIv 为空则使用全0
     * @return 加密过的数据(经过Base64编码)
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    @Throws(GeneralSecurityException::class, UnsupportedEncodingException::class)
    fun encrypt(data: String, base64edKey: String, base64edIv: String?): String {
        val keyByte: ByteArray = Base64.decodeBase64(base64edKey)
        return Base64.encodeBase64String(encrypt(StringUtils.getBytesUtf8(data), keyByte, if (base64edIv == null) NULL_IV else Base64.decodeBase64(base64edIv)))
    }

//    private val mEncryptCipher: Cipher = Cipher.getInstance("DES")
//
//
//    /**
//     * 对 字符串 加密
//     */
//    fun encrypt(strIn: String): String {
//        return byte2HexStr(encrypt(strIn.toByteArray()))
//    }
//
//
//    /**
//     * 对 字节数组 加密
//     */
//    @Throws(java.lang.Exception::class)
//    fun encrypt(arrB: ByteArray): ByteArray {
//        return mEncryptCipher.doFinal(arrB)
//    }
//
//
//    /**
//     * HEX转码 String to Byte
//     */
//    @Throws(Exception::class)
//    fun hexStr2Byte(strIn: String): ByteArray? {
//        val arrB = strIn.toByteArray()
//        val iLen = arrB.size
//        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
//        val arrOut = ByteArray(iLen / 2)
//        var i = 0
//        while (i < iLen) {
//            val strTmp = String(arrB, i, 2)
//            arrOut[i / 2] = strTmp.toInt(16).toByte()
//            i += 2
//        }
//        return arrOut
//    }
//
//    /**
//     * HEX转码 Byte to  String
//     */
//    @Throws(Exception::class)
//    fun byte2HexStr(arrB: ByteArray): String {
//        val iLen = arrB.size
//        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
//        val sb = StringBuffer(iLen * 2)
//        for (i in 0 until iLen) {
//            var intTmp = arrB[i].toInt()
//            // 把负数转换为正数
//            while (intTmp < 0) {
//                intTmp += 256
//            }
//            // 小于0F的数需要在前面补0
//            if (intTmp < 16) {
//                sb.append("0")
//            }
//            sb.append(intTmp.toString(16))
//        }
//        return sb.toString()
//    }


}