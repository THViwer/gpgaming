package com.onepiece.gpgaming.player.controller.chain

import com.onepiece.gpgaming.games.http.OkHttpUtil
import okhttp3.Request
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
open class ChainUtil(
        private val okHttpUtil: OkHttpUtil
) {

    @Async
    open fun clickRv(chainCode: String?) {

        if (chainCode == null) return

        val url = "https://m9s.co/rv/${chainCode}"

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