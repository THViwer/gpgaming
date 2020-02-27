package com.onepiece.gpgaming.games.live

import org.apache.commons.codec.binary.Base64
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec

object EBetSignUtil  {

    fun sign(data: String, privateKey: String ): String {
        val keyBytes = Base64.decodeBase64(privateKey)

        val pkcs8EncodedKeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")

        val priKey: PrivateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec)
        val signature: Signature = Signature.getInstance("MD5withRSA")
        signature.initSign(priKey)
        signature.update(data.toByteArray())

        return signature.sign().let {
            Base64.encodeBase64String(it)
        }
    }

    fun verify(data: String, publicKey: String, sign: String) {

    }

    //    //公钥匙验证
//    public static boolean verify(byte[] data,String publicKey,String sign)throws Exception{
//        byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
//        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);
//        Signature signature = Signature.getInstance("MD5withRSA");
//        signature.initVerify(publicKey2);
//        signature.update(data);
//        return signature.verify(Base64.getDecoder().decode(sign));
//    }
}