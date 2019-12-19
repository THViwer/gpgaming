package com.onepiece.gpgaming.core.service.impl

import com.onepiece.gpgaming.beans.model.TaskTimerType
import com.onepiece.gpgaming.core.dao.TaskTimerDao
import com.onepiece.gpgaming.core.service.TaskTimerService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TaskTimerServiceImpl(
        private val taskTimerDao: TaskTimerDao
) : TaskTimerService {

    override fun lock(day: LocalDate, type: TaskTimerType): Boolean {
        return taskTimerDao.lock(day = day, type = type)
    }

    override fun done(day: LocalDate, type: TaskTimerType): Boolean {
        return taskTimerDao.done(day = day, type = type)
    }

    override fun fail(day: LocalDate, type: TaskTimerType): Boolean {
        return taskTimerDao.fail(day = day, type = type)
    }
}