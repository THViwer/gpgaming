package com.onepiece.gpgaming.core.dao

import com.onepiece.gpgaming.beans.model.PullOrderTask
import com.onepiece.gpgaming.core.dao.basic.BasicDao

interface PullOrderTaskDao: BasicDao<PullOrderTask> {

    fun create(task: PullOrderTask): Boolean

}