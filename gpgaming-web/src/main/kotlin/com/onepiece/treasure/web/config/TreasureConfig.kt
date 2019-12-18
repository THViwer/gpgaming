package com.onepiece.treasure.web.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "bgv.config")
@Component
class BgvConfig {

    lateinit var title: String

    lateinit var updatePath: String

    lateinit var sharePath: String

    lateinit var shareContent: String

    var videoParse: Boolean = false

    var freeWatchSequence: Int = 0

    lateinit var serviceQQ: String

    lateinit var apkDownloadMethod: String

    var starGirls: ArrayList<StarGirl> = arrayListOf()

    var startFee: Boolean = false


}

class StarGirl {

    lateinit var name: String

    lateinit var path: String
}