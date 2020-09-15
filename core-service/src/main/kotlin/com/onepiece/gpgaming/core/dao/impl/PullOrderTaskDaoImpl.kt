package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.enums.Platform
import com.onepiece.gpgaming.beans.model.PullOrderTask
import com.onepiece.gpgaming.core.dao.PullOrderTaskDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PullOrderTaskDaoImpl : PullOrderTaskDao, BasicDaoImpl<PullOrderTask>("pull_order_task") {

    override val mapper: (rs: ResultSet) -> PullOrderTask
        get() = { rs ->
            val id = rs.getInt("id")
            val clientId = rs.getInt("client_id")
            val platform = rs.getString("platform").let { Platform.valueOf(it) }
            val path = rs.getString("path")
            val param = rs.getString("param")
            val response = rs.getString("response")
            val message = rs.getString("message")
            val type = rs.getString("type").let { PullOrderTask.OrderTaskType.valueOf(it) }
            val ok = rs.getBoolean("ok")
            val startTime = rs.getTimestamp("start_time").toLocalDateTime()
            val endTime = rs.getTimestamp("end_time").toLocalDateTime()

            PullOrderTask(id = id, clientId = clientId, platform = platform, path = path, param = param, response = response,
                    type = type, ok = ok, startTime = startTime, endTime = endTime, message = message)

        }

    override fun create(task: PullOrderTask): Boolean {
        return insert()
                .set("client_id", task.clientId)
                .set("platform", task.platform)
                .set("path", task.path)
                .set("param", task.param)
                .set("response", task.response)
                .set("type", task.type)
                .set("ok", task.ok)
                .set("start_time", "${task.startTime}")
                .set("end_time", "${task.endTime}")
                .executeOnlyOne()

    }


}