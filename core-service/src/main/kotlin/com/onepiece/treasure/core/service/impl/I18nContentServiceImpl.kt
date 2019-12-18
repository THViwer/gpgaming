package com.onepiece.treasure.core.service.impl

import com.onepiece.treasure.beans.enums.I18nConfig
import com.onepiece.treasure.beans.exceptions.OnePieceExceptionCode
import com.onepiece.treasure.beans.model.I18nContent
import com.onepiece.treasure.beans.value.database.I18nContentCo
import com.onepiece.treasure.beans.value.database.I18nContentUo
import com.onepiece.treasure.core.dao.I18nContentDao
import com.onepiece.treasure.core.service.I18nContentService
import org.springframework.stereotype.Service

@Service
class I18nContentServiceImpl(
        private val i18nContentDao: I18nContentDao
) : I18nContentService {

    override fun create(i18nContentCo: I18nContentCo): Int {
        val id = i18nContentDao.create(i18nContentCo)
        check(id > 0) { OnePieceExceptionCode.DB_CHANGE_FAIL }
        return id
    }

    override fun update(i18nContentUo: I18nContentUo) {
//        val i18nContent = i18nContentDao.get(i18nContentUo.id)
        val state = i18nContentDao.update(i18nContentUo)
        check(state) { OnePieceExceptionCode.DB_CHANGE_FAIL }

//        val redisKey = when (i18nContent.configType) {
//            I18nConfig.Promotion -> OnePieceRedisKeyConstant.promotions(i18nContent.clientId)
//            I18nConfig.Announcement -> OnePieceRedisKeyConstant.lastAnnouncement(i18nContent.clientId)
//        }
//        redisService.delete(redisKey)
    }

    override fun getConfigType(clientId: Int, configType: I18nConfig): List<I18nContent> {
//        return i18nContentDao.all(clientId)
        return i18nContentDao.getConfigType(clientId = clientId, configType = configType)
//                .map {
//            I18nContentVo(id = it.id, title = it.title, synopsis = it.synopsis, content = it.content, language = it.language,
//                    configId = it.configId, configType = it.configType, banner = it.banner, precautions = it.precautions)
//        }
    }

    override fun getConfigs(clientId: Int): List<I18nContent> {
        return i18nContentDao.all(clientId)
    }

    //    override fun getConfigType(clientId: Int, configType: I18nConfig, language: Language): I18nContent {
//
//        this.getConfigType(clientId = clientId, configType = configType)
//
//    }
}