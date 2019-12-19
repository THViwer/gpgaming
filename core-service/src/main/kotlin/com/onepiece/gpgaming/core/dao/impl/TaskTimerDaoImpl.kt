package com.onepiece.gpgaming.core.dao.impl

import com.onepiece.gpgaming.beans.model.TaskTimer
import com.onepiece.gpgaming.beans.model.TaskTimerState
import com.onepiece.gpgaming.beans.model.TaskTimerType
import com.onepiece.gpgaming.core.dao.TaskTimerDao
import com.onepiece.gpgaming.core.dao.basic.BasicDaoImpl
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class TaskTimerDaoImpl : BasicDaoImpl<TaskTimer>("task_timer"), TaskTimerDao {

    override val mapper: (rs: ResultSet) -> TaskTimer
        get() = { rs ->
            val id = rs.getInt("id")
            val day = rs.getDate("day").toLocalDate()
            val state = rs.getString("state").let { TaskTimerState.valueOf(it) }
            val type = rs.getString("type").let { TaskTimerType.valueOf(it) }
            val createdTime = rs.getTimestamp("created_time").toLocalDateTime()
            val updatedTime = rs.getTimestamp("updated_time").toLocalDateTime()
            TaskTimer(id = id, day = day, state = state, type = type, createdTime = createdTime, updatedTime = updatedTime)

        }

    override fun lock(day: LocalDate, type: TaskTimerType): Boolean {
        return this.insert()
                .set("day", day)
                .set("type", type)
                .set("state", TaskTimerState.Process)
                .executeOnlyOne()

    }

    override fun done(day: LocalDate, type: TaskTimerType): Boolean {

        return update()
                .set("state", TaskTimerState.Done)
                .where("day", day)
                .where("type", type)
                .executeOnlyOne()

    }

    override fun fail(day: LocalDate, type: TaskTimerType): Boolean {
        return update()
                .set("state", TaskTimerState.Fail)
                .where("day", day)
                .where("type", type)
                .executeOnlyOne()
    }
}