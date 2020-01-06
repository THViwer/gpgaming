package com.onepiece.gpgaming.games.combination

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.onepiece.gpgaming.beans.enums.GameCategory
import com.onepiece.gpgaming.beans.enums.Language
import com.onepiece.gpgaming.beans.enums.LaunchMethod
import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.value.internet.web.SlotCategory
import com.onepiece.gpgaming.beans.value.internet.web.SlotGame
import com.onepiece.gpgaming.utils.AwsS3Util
import java.io.File
import java.util.*
import java.util.stream.Collectors

object AsiaGamingUtil {


    fun upload(launch: LaunchMethod, language: Language): List<SlotGame> {
        val file = File("/Users/cabbage/Downloads/asia_gaming_origin.csv")
        val data = file.readLines().map { line ->
            val list = line.split(",")
            val cname = list[0]
            val ename = list[2]

            val gameId = if (launch == LaunchMethod.Web) list[4] else list[5]
            gameId.let { if (it.contains("n/a") ) null else it }
                    ?.let {
                        Han1(cname = cname, ename = ename, gameId = it)
                    }
        }.filterNotNull().map { it.cname to it }.toMap()


        return File("/Users/cabbage/Downloads/asia_gaming_done.csv")
                .readLines()
                .filter { it.split(",").first().isNotEmpty() }
                .parallelStream()
                .map { line ->
                    val list = line.split(",")
                    val cname = list[1].replace(" ", "")
                    val ename = list[0]
                    val type = list[2].let { GameCategory.valueOf(it) }

                    data[cname]?.let {
                        val gameId = it.gameId

                        val lang = if (language == Language.CN) "ZH" else "EN"

                        val xinFile = File("/Users/cabbage/Downloads/xin/${gameId}_${lang}.gif")
                                .let { if (it.exists()) it else File("/Users/cabbage/Downloads/xin/${gameId}_${lang}.png") }
                                .let { if (it.exists()) it else File("/Users/cabbage/Downloads/xin/${gameId}_${lang} 2.png") }
                                .let { if (it.exists()) it else null }

                        if (xinFile == null) {
                            println("gameId = ${gameId}, cname = ${cname}, 未找到图片")
                        }

                        xinFile?.let {
                            val path = AwsS3Util.uploadLocalFile(it, "slot/asia_game/${it.name}")
                            val gameName = if (language == Language.CN) cname else ename
                            SlotGame(platform = Platform.AsiaGamingSlot, gameId = gameId, category = type, gameName = gameName, hot = false, new = false,
                                    icon = path, touchIcon = "-", status = Status.Normal)
//                            println("gameId = $gameId, cname = $cname, ename = $ename, type = $type")
                        }
                    }
                }.collect(Collectors.toList())
                .filterNotNull()


    }

    data class Han1(
            val cname: String,

            val ename: String,

            val gameId: String


    )
}

fun main() {

    // asia_gaming_done.xlsx

    listOf(Language.CN, Language.EN)
            .forEach { language ->

                listOf(LaunchMethod.Wap, LaunchMethod.Web).forEach { launch ->

                    val games = AsiaGamingUtil.upload(LaunchMethod.Web, Language.CN)

                    val hots = hashSetOf<String>()
                    val news = hashSetOf<String>()

                    val categories = games.map {
                        if (it.category == GameCategory.Hot) {
                            hots.add(it.gameId)
                        }
                        if (it.category == GameCategory.New) {
                            news.add(it.gameId)
                        }

                        val hot = hots.contains(it.gameId)
                        val new = news.contains(it.gameId)

                        it.copy(hot = hot, new = new)
                    }.groupBy { it.category }
                            .map { SlotCategory(gameCategory = it.key, games = it.value) }

                    val json = jacksonObjectMapper().writeValueAsString(categories)
                    val jsonFile = File("/Users/cabbage/Desktop/${UUID.randomUUID()}.json")
                    jsonFile.writeBytes(json.toByteArray())

                    AwsS3Util.uploadLocalFile(jsonFile, "slot/asia_gaming_${launch.name.toLowerCase()}_${language.name.toLowerCase()}.json")

                    jsonFile.delete()
                }
            }

}