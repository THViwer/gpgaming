//package com.onepiece.treasure.task
//
//import com.fasterxml.jackson.dataformat.xml.XmlMapper
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.fasterxml.jackson.module.kotlin.readValue
//import com.onepiece.treasure.beans.enums.GameCategory
//import com.onepiece.treasure.beans.value.internet.web.SlotCategory
//import com.onepiece.treasure.beans.value.internet.web.SlotGame
//import com.onepiece.treasure.games.bet.JacksonMapUtil
//import com.onepiece.treasure.games.http.OkHttpUtil
//import com.onepiece.treasure.utils.AwsS3Util
//import java.io.File
//
//object SlotGameIconTest {
//    val objectMapper = jacksonObjectMapper()
//    val xmlMapper = XmlMapper()
//
//    val okHttpUtil = OkHttpUtil(objectMapper = objectMapper, xmlMapper = xmlMapper)
//
//    fun test(games: List<SlotGame>) {
//
//        val iconTestMap = games.map {
//            try {
//                okHttpUtil.doGet(it.icon, String::class.java)
//                true to it
//            } catch (e: Exception) {
//                false to it
//            }
//        }.toMap()
//
//        println("$iconTestMap")
//
//    }
//
//
//
//}
//
//
//fun main() {
////    val name = "joker_wap.json"
////
////    val url = "https://s3.ap-southeast-1.amazonaws.com/awspg1/slot/$name"
////
////    val json = SlotGameIconTest.okHttpUtil.doGet(url = url, clz = String::class.java)
////    val json1 = SlotGameIconTest.objectMapper.readValue<List<JacksonMapUtil>>(json)
////    val a = json1.map { it.mapUtil.asList("games") }.reduce { acc, list ->  acc.plus(list)}.map { it.data }
////    val b = SlotGameIconTest.objectMapper.writeValueAsString(a)
////    val c = SlotGameIconTest.objectMapper.readValue<List<SlotGame>>(b)
//
////    val objectmapper = jacksonObjectMapper()
////    val json = String(File("/Users/cabbage/Desktop/joker1.json").readBytes())
////    val categories = objectmapper.readValue<List<SlotCategory>>(json)
////
////    val games = categories.map { it.games }.reduce { acc, list ->  acc.plus(list)}
////
////
////    val newGames = games.map { it.copy(icon = it.icon.replace("////", "//"), touchIcon = it.touchIcon?.replace("////", "")) }
////
////    // 分类
////    val csvfile = File("/Users/cabbage/workspace/onepiece/treasure/gamefile/joker_wap.csv_Done.csv")
////    val categoryMap = csvfile.readLines().map {
////        val lines = it.split(",")
////        val name = lines[0]
////        val category = lines[1]
////        if (name.isBlank() || category.isBlank()) {
////            null
////        } else {
////            name to category.let { GameCategory.valueOf(it) }
////        }
////    }.filterNotNull().toMap()
//
//
//    val objectMapper = jacksonObjectMapper()
//    val url = "https://s3.ap-southeast-1.amazonaws.com/awspg1/slot/joker.json"
//
//    val json = SlotGameIconTest.okHttpUtil.doGet(url, String::class.java)
//    val categories = objectMapper.readValue<List<SlotCategory>>(json)
//    val hots = hashSetOf<String>()
//    val news = hashSetOf<String>()
//    val newCategories = categories.map { sc ->
//
//        val games = sc.games.map {
//
//
//            if (sc.gameCategory == GameCategory.Hot) {
//                hots.add(it.gameId)
//            } else if (sc.gameCategory == GameCategory.New) {
//                news.add(it.gameId)
//            }
//
//            val hot = hots.contains(it.gameId)
//            val new = news.contains(it.gameId)
//
//            it.copy(icon = it.icon.replace("////", "//"), touchIcon = it.touchIcon?.replace("////", "//"), hot = hot, new = new)
//        }
//        sc.copy(games = games)
//    }.sortedBy { it.gameCategory.sort }
//
//
//    val saveJson = objectMapper.writeValueAsString(newCategories)
//    val file = File("/Users/cabbage/Desktop/joker_wap.json")
//    file.writeBytes(saveJson.toByteArray())
//
//
//    AwsS3Util.uploadLocalFile(file, "slot/joker.json")
//
//
////
//
////    val data = newGames.map {
////        it.copy(category = categoryMap[it.gameId]?: GameCategory.Default)
////    }.groupBy { it.category }.map { SlotCategory(gameCategory = it.key, games = it.value) }
////
////
////    val newJson = SlotGameIconTest.objectMapper.writeValueAsString(data)
////    val file = File("/Users/cabbage/Desktop/$name")
////    file.writeBytes(newJson.toByteArray())
////
////    AwsS3Util.uploadLocalFile(file, "slot/$name")
//
//}
//
