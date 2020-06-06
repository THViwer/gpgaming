package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.Blog
import com.onepiece.gpgaming.beans.value.database.BlogValue
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface BlogDao: BasicDao<Blog> {

    fun list(clientId: Int): List<Blog>

    fun create(co: BlogValue.BlogCo): Boolean

    fun update(uo: BlogValue.BlogUo): Boolean

}