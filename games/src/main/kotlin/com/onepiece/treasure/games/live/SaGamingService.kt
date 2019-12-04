package com.onepiece.treasure.games.live

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.onepiece.treasure.beans.model.token.SaGamingClientToken
import com.onepiece.treasure.games.GameValue
import com.onepiece.treasure.games.PlatformService
import com.onepiece.treasure.games.http.OkHttpUtil
import okhttp3.FormBody
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.net.URLEncoder
import java.security.Key
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec


/**
 * 密钥算法
 * java支持56位密钥，bouncycastle支持64位
 */
const val KEY_ALGORITHM = "DES"

/**
 * 加密/解密算法/工作模式/填充方式
 */
const val CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding"
/**
 *
 * 生成密钥，java6只支持56位密钥，bouncycastle支持64位密钥
 * @return byte[] 二进制密钥
 */
@Throws(Exception::class)
fun initkey(): ByteArray? { //实例化密钥生成器
    val kg: KeyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM)
    //初始化密钥生成器
    kg.init(56)
    //生成密钥
    val secretKey: SecretKey = kg.generateKey()
    //获取二进制密钥编码形式
    return secretKey.encoded
}

/**
 * 转换密钥
 * @param key 二进制密钥
 * @return Key 密钥
 */
@Throws(Exception::class)
fun toKey(key: ByteArray?): Key { //实例化Des密钥
    val dks = DESKeySpec(key)
    //实例化密钥工厂
    val keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM)
    //生成密钥
    return keyFactory.generateSecret(dks)
}

/**
 * 加密数据
 * @param data 待加密数据
 * @param key 密钥
 * @return byte[] 加密后的数据
 */
@Throws(Exception::class)
fun encrypt(data: String, key: String): String { //还原密钥
    val k: Key = toKey(key.toByteArray())
    //实例化
    val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
    //初始化，设置为加密模式
    cipher.init(Cipher.ENCRYPT_MODE, k)
    //执行操作
    val byte =  cipher.doFinal(data.toByteArray())
    return Base64.encodeBase64String(byte)
}

//fun main() {
//    val param = "method=RegUserInfo&Key=g9G16nTs&Time=20191204203051&Username=01000016aw&CurrencyType=MYR"
//    val encryptKey = "g9G16nTs"
//    val x = encrypt(param.toByteArray(), encryptKey.toByteArray()).let { Base64.encodeBase64String(it) }
//
//    println(x)
//}

val dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyyMMddHHmmss")


fun main() {

    val objectMapper = jacksonObjectMapper()
    val xmlMapper = XmlMapper()

    val okHttpUtil = OkHttpUtil(objectMapper, xmlMapper)


    val time = LocalDateTime.now().format(dateTimeFormatter)

    val param = mapOf(
            "method" to "RegUserInfo",
            "Key" to "08EFED20ECEC405F802246F1F0603CE4",
            "Time" to time,
            "Username" to "Ccaa234As",
            "CurrencyType" to "MYR"
    )

    val signParam = param.map { "${it.key}=${it.value}" }.joinToString(separator = "&")
    println(signParam)

    val desSign = encrypt(data = signParam, key = "g9G16nTs").let { URLEncoder.encode(it, "utf-8") }
    println(desSign)
//
    val md5Param = "${signParam}GgaIMaiNNtg${time}08EFED20ECEC405F802246F1F0603CE4"
    val md5Sign = DigestUtils.md5Hex(md5Param)
    println(md5Sign)
//
////        val url = "http://sai-api.sa-apisvr.com/api/api.aspx"
//
    val formBody = FormBody.Builder()
            .add("method", "RegUserInfo")
            .add("Key", "08EFED20ECEC405F802246F1F0603CE4")
            .add("Time", time)
            .add("Username", "Ccaa234As")
            .add("CurrencyType", "MYR")
            .add("q", desSign)
            .add("s", md5Sign)
            .build()
    val url = "http://94.237.64.70:2008"
    val xml = okHttpUtil.doPostForm(url = url, body = formBody, clz = String::class.java)
    println(xml)

}




@Service
class SaGamingService(
        val xmlMapper: XmlMapper
) : PlatformService() {

    /**
     *
    Secret Key 密鑰: 08EFED20ECEC405F802246F1F0603CE4
    MD5Key MD5鍵: GgaIMaiNNtg
    EncryptKey 加密鍵: g9G16nTs
    SA APP EncryptKey 加密鍵: M06!1OgI

    我司根據不同功能設定了兩組API路徑。
    Generic通用API路徑: http://sai-api.sa-apisvr.com/api/api.aspx
    Get Bet Detail取得會員下注詳情API路徑 : http://sai-api.sa-rpt.com/api/api.aspx
     */

    val dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyyMMddHHmmss")

//    fun encrypt(data: String, key: String): String {
////        val keyFactory = SecretKeyFactory.getInstance("DES")
////        val sec = keyFactory.generateSecret(DESedeKeySpec(key))
////        val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
////
////        val ivParameterSpec = IvParameterSpec(iv ?: DesUtil.NULL_IV)
////        cipher.init(Cipher.ENCRYPT_MODE, sec, ivParameterSpec)
////        return cipher.doFinal(data)
//        val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
//        val dataBytes: ByteArray = data.toByteArray()
//
//        val keyspec = SecretKeySpec(key.toByteArray(), "DES")
//
//        val ivspec = IvParameterSpec(DesUtil.NULL_IV)
//        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec)
//
//        val encrypted = cipher.doFinal(dataBytes)
//        return Base64.encodeBase64String(encrypted)
//    }


    override fun register(registerReq: GameValue.RegisterReq): String {
        val clientToken = registerReq.token as SaGamingClientToken

        val time = LocalDateTime.now().format(dateTimeFormatter)
        val formBody = FormBody.Builder()
                .add("method", "RegUserInfo")
                .add("Key", "08EFED20ECEC405F802246F1F0603CE4")
                .add("Time", time)
                .add("Username", registerReq.username)
                .add("CurrencyType", "MYR")
                .build()


        val param = mapOf(
                "method" to "RegUserInfo",
                "Key" to "g9G16nTs",
                "Time" to time,
                "Username" to registerReq.username,
                "CurrencyType" to "MYR"
        )
        val signParam = param.map { "${it.key}=${it.value}" }.joinToString(separator = "&")

        val desSign = encrypt(data = signParam, key = "g9G16nTs")

        val md5Param = "$signParam${clientToken.md5Key}${time}${clientToken.secretKey}"
        val md5Sign = DigestUtils.md5Hex(md5Param)

//        val url = "http://sai-api.sa-apisvr.com/api/api.aspx"
        val url = "http://94.237.64.70:2008?q=$desSign&s=$md5Sign"
        val xml = okHttpUtil.doPostForm(url = url, body = formBody, clz = String::class.java)
        val result = xmlMapper.readValue<SaGamingValue.Result>(xml)

        error("")
    }

    override fun balance(balanceReq: GameValue.BalanceReq): BigDecimal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun transfer(transferReq: GameValue.TransferReq): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkTransfer(checkTransferReq: GameValue.CheckTransferReq): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}