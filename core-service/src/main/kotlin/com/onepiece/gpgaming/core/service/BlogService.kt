package com.onepiece.gpgaming.core.service

import com.onepiece.gpgaming.beans.value.database.BlogValue

interface BlogService {

    fun list(clientId: Int): List<BlogValue.BlogVo>

    fun normalList(clientId: Int): List<BlogValue.BlogVo>

    fun create(co: BlogValue.BlogCo)

    fun update(uo: BlogValue.BlogUo)



}