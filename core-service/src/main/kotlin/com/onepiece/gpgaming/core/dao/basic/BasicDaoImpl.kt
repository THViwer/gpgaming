package com.onepiece.gpgaming.core.dao.basic

import com.onepiece.gpgaming.utils.BatchInsert
import com.onepiece.gpgaming.utils.Insert
import com.onepiece.gpgaming.utils.JdbcBuilder
import com.onepiece.gpgaming.utils.Query
import com.onepiece.gpgaming.utils.Update
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet

abstract class BasicDaoImpl<T>(
        private val table: String
): BasicDao<T> {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    abstract val mapper: (rs: ResultSet) -> T


    override fun get(id: Int): T {
        return query(includeStatus = false).where("id", id).executeOnlyOne(mapper)
    }

    override fun all(clientId: Int): List<T> {
        return query().where("client_id", clientId).execute(mapper)
    }

    override fun all(): List<T> {
        return query().execute(mapper)
    }

    fun insert(defaultTable: String? = null): Insert {
        return JdbcBuilder.insert(jdbcTemplate, defaultTable?: table)
    }

    fun <T> batchInsert(data: List<T>): BatchInsert<T> {
        return JdbcBuilder.batchInsert(jdbcTemplate, table, data)
    }

    fun query(returnColumns: String? = null, defaultTable: String? = null, includeStatus: Boolean = true): Query {
        return Query(jdbcTemplate, defaultTable?: table, returnColumns, includeStatus)
    }

    fun update(defaultTable: String? = null): Update {
        return Update(jdbcTemplate, defaultTable?: table)
    }


}