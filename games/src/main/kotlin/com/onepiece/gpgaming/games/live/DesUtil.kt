package com.onepiece.gpgaming.games.live

import org.apache.commons.codec.binary.Base64
import java.math.BigInteger
import java.security.MessageDigest
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec


fun main() {

    val encryptKey = "g9G16nTs".toByteArray()
    val keySpec: KeySpec = DESKeySpec(encryptKey)
    val myDesKey = SecretKeyFactory.getInstance("DES").generateSecret(keySpec)
    val iv = IvParameterSpec(encryptKey)

    // Create the cipher
    // Create the cipher
    val desCipher: Cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")

    // Initialize the cipher for encryption
    // Initialize the cipher for encryption
    desCipher.init(Cipher.ENCRYPT_MODE, myDesKey, iv)

    //sensitive information
    //sensitive information
    val source = "method=RegUserInfo&Key=08EFED20ECEC405F802246F1F0603CE4&Time=20191205132839&Username=Ccaa234As&CurrencyType=MYR"
    val text = source.toByteArray()

    println("Text [Byte Format] : $text")
    println("Text : " + String(text))

    // Encrypt the text
    // Encrypt the text
    val textEncrypted: ByteArray = desCipher.doFinal(text)
    val t: String = Base64.encodeBase64String(textEncrypted)

    println("Text Encryted [Byte Format] : $textEncrypted")
    println("Text Encryted : $t")

    // Initialize the same cipher for decryption
    // Initialize the same cipher for decryption
    desCipher.init(Cipher.DECRYPT_MODE, myDesKey, iv)

    // Decrypt the text
    // Decrypt the text
    val textDecrypted: ByteArray = desCipher.doFinal(textEncrypted)

    println("Text Decryted : " + String(textDecrypted))


    // MD5 samples
    // MD5 samples
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(text)
    val bigInt = BigInteger(1, digest)
    val hashtext = bigInt.toString(16)
    println("MD5 of $source is $hashtext")
}