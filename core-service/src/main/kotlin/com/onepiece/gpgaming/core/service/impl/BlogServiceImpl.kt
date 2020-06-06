package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.enums.I18nConfig
import com.onepiece.gpgaming.beans.enums.Status
import com.onepiece.gpgaming.beans.exceptions.OnePieceExceptionCode
import com.onepiece.gpgaming.beans.value.database.BlogValue
import com.onepiece.gpgaming.core.dao.BlogDao
import com.onepiece.gpgaming.core.service.BlogService
import com.onepiece.gpgaming.core.service.I18nContentService
import org.springframework.stereotype.Service

@Service
class BlogServiceImpl(
        private val blogDao: BlogDao,
        private val i18nContentService: I18nContentService
) : BlogService {

    override fun list(clientId: Int): List<BlogValue.BlogVo> {

        val list = blogDao.list(clientId = clientId)

        val contents = i18nContentService.getConfigs(clientId = clientId)
                .filter { it.configType == I18nConfig.Blog }
                .groupBy { it.configId}

        return list.map { blog ->
            val cs = contents[blog.id] ?: emptyList()
            BlogValue.BlogVo(id = blog.id, title = blog.title, headImg = blog.headImg, sort = blog.sort, platform = blog.platform, status = blog.status,
                    contents = cs)
        }
    }

    override fun normalList(clientId: Int): List<BlogValue.BlogVo> {
        return this.list(clientId = clientId).filter { it.status == Status.Normal }
    }

    override fun create(co: BlogValue.BlogCo) {
        val flag = blogDao.create(co = co)
        check(flag) { OnePieceExceptionCode.DATA_FAIL }
    }

    override fun update(uo: BlogValue.BlogUo) {
        val flag = blogDao.update(uo = uo)
        check(flag) { OnePieceExceptionCode.DATA_FAIL }
    }
}