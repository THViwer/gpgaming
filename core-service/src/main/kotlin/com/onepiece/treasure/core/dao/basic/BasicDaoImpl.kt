package com.onepiece.treasure.core.dao.basic

import com.onepiece.treasure.utils.Insert
import com.onepiece.treasure.utils.JdbcBuilder
import com.onepiece.treasure.utils.Query
import com.onepiece.treasure.utils.Update
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet

abstract class BasicDaoImpl<T>(
        private val table: String
): BasicDao<T> {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    abstract fun mapper(): (rs: ResultSet) -> T


    override fun get(id: Int): T {
        return query().where("id", id).executeOnlyOne(mapper())
    }

    override fun all(clientId: Int): List<T> {
        return query().where("client_id", clientId).execute(mapper())
    }

    override fun all(): List<T> {
        return query().execute(mapper())
    }

    fun insert(): Insert {
        return JdbcBuilder.insert(jdbcTemplate, table)
    }

    fun query(returnColumns: String? = null): Query {
        return Query(jdbcTemplate, table, returnColumns)
    }

    fun update(): Update {
        return Update(jdbcTemplate, table)
    }

}