package com.onepiece.treasure.core.dao.basic

import com.onepiece.treasure.utils.*
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
        return query().where("id", id).executeOnlyOne(mapper)
    }

    override fun all(clientId: Int): List<T> {
        return query().where("client_id", clientId).execute(mapper)
    }

    override fun all(): List<T> {
        return query().execute(mapper)
    }

    fun insert(): Insert {
        return JdbcBuilder.insert(jdbcTemplate, table)
    }

    fun <T> batchInsert(data: List<T>): BatchInsert<T> {
        return JdbcBuilder.batchInsert(jdbcTemplate, table, data)
    }

    fun query(returnColumns: String? = null): Query {
        return Query(jdbcTemplate, table, returnColumns)
    }

    fun update(): Update {
        return Update(jdbcTemplate, table)
    }


}