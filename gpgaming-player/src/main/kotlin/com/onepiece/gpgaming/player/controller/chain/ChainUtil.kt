package com.onepiece.gpgaming.player.controller.chain

import com.onepiece.gpgaming.games.http.OkHttpUtil
import com.onepiece.gpgaming.utils.RequestUtil
import okhttp3.Request
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.*

@Component
open class ChainUtil(
        private val okHttpUtil: OkHttpUtil
) {

    @Async
    open fun clickRv(chainCode: String?) {

        if (chainCode == null) return

        val ip = RequestUtil.getIpAddress()
        val url = "https://m9s.co/rv/${chainCode}?customIp=$ip&hash=${UUID.randomUUID()}"

        val request = Request.Builder()
                .url(url)
                .get()
                .build()

        try {
            okHttpUtil.client.newCall(request).execute()
        } catch (e: Exception) {

        }
    }

}