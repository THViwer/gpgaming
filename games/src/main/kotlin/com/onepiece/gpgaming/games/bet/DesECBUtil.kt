package com.onepiece.gpgaming.games.bet

import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object DesECBUtil {

    /**
     * 加密<br></br>
     * @param data 被解码的数据(注意编码转换)
     * @param key key
     * @param iv 向量(必须为8byte)
     * @return
     * @throws GeneralSecurityException
     */
    fun encrypt(data: String, key: String): String {

        val keySpec = SecretKeySpec(key.toByteArray(), "DES")
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        return String(cipher.doFinal(data.toByteArray()))
    }


}