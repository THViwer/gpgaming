package com.onepiece.treasure.web.controller

import com.onepiece.treasure.core.dao.LevelDao
import com.onepiece.treasure.beans.model.Level
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/demo")
class DemoController(
        private val levelDao: LevelDao
) {

    @GetMapping
    fun test(): List<Level> {
        return levelDao.all()
    }

}