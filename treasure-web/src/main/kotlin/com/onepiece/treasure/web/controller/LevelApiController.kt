package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.Status
import com.onepiece.treasure.beans.value.database.LevelCo
import com.onepiece.treasure.beans.value.database.LevelUo
import com.onepiece.treasure.beans.value.internet.web.*
import com.onepiece.treasure.core.service.LevelService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/level")
class LevelApiController(
        private val levelService: LevelService
) : BasicController(), LevelApi {

    @GetMapping
    override fun all(): List<LevelVo> {
        return levelService.all(clientId).map {
            with(it) {
                //TODO 查询层级总人数
                LevelVo(id = id, name = name, status = status, createdTime = createdTime, total = 100)
            }
        }
    }

    @GetMapping("/normal")
    override fun normalList(): List<LevelVo> {
        return all().filter { it.status == Status.Normal }
    }

    @PostMapping
    override fun create(@RequestBody levelCoReq: LevelCoReq) {
        val levelCo = LevelCo(clientId = clientId, name = levelCoReq.name)
        levelService.create(levelCo)
    }

    @PutMapping
    override fun update(@RequestBody levelUoReq: LevelUoReq) {
        val levelUo = LevelUo(id = levelUoReq.id, name = levelUoReq.name, status = levelUoReq.status)
        levelService.update(levelUo)
    }

    @PutMapping("/move")
    override fun move(@RequestBody levelMoveDo: LevelMoveDo): LevelMoveVo {
        return LevelValueFactory.generatorLevelMoveVo()
    }

    @GetMapping("/move/check/{sequence}")
    override fun checkMove(@PathVariable sequence: String): LevelMoveCheckVo {
        return LevelValueFactory.generatorLevelMoveCheckVo()
    }
}