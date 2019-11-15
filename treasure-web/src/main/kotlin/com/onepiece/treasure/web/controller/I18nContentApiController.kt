package com.onepiece.treasure.web.controller

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.enums.Language
import com.onepiece.treasure.beans.value.database.I18nContentCo
import com.onepiece.treasure.beans.value.database.I18nContentUo
import com.onepiece.treasure.beans.value.internet.web.I18nContentCoReq
import com.onepiece.treasure.beans.value.internet.web.I18nContentVo
import com.onepiece.treasure.core.service.I18nContentService
import com.onepiece.treasure.web.controller.basic.BasicController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/i18n")
class I18nContentApiController(
        private val i18nContentService: I18nContentService
) : BasicController(), I18nContentApi {

    @GetMapping("/languages")
    override fun languages(): List<Language> {
        return Language.values().toList()
    }

    @GetMapping("/announcement")
    override fun all(): List<I18nContentVo> {
        return i18nContentService.getConfigType(clientId = clientId, configType = I18nConfig.Announcement)
    }

    @PostMapping
    override fun create(@RequestBody i18nContentCoReq: I18nContentCoReq) {

        val i18nContentCo = I18nContentCo(clientId = clientId, title = i18nContentCoReq.title, synopsis = i18nContentCoReq.synopsis, content = i18nContentCoReq.content,
                language = i18nContentCoReq.language, configId = i18nContentCoReq.configId, configType = i18nContentCoReq.configType)

        i18nContentService.create(i18nContentCo)


    }

    @PutMapping
    override fun update(@RequestBody i18nContentVo: I18nContentVo) {
        val i18nContentUo = I18nContentUo(id = i18nContentVo.id, title = i18nContentVo.title, synopsis = i18nContentVo.synopsis, content = i18nContentVo.content,
                language = i18nContentVo.language)
        i18nContentService.update(i18nContentUo)

    }
}