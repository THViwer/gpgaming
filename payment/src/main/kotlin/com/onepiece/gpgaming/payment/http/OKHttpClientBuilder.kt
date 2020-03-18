package com.onepiece.gpgaming.payment.http

import okhttp3.OkHttpClient

import java.security.KeyManagementException

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object OKHttpClientBuilder {

    fun buildOKHttpClient(): OkHttpClient.Builder {
        return try {
            val trustAllCerts = buildTrustManagers()
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier(HostnameVerifier { _: String?, _: SSLSession? -> true })
            builder
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            OkHttpClient.Builder()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
            OkHttpClient.Builder()
        }
    }

    private fun buildTrustManagers(): Array<TrustManager> {
        return arrayOf(
                object: X509TrustManager {
                    override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    }

                    override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return emptyArray()
                    }
                }
        )

    }
}