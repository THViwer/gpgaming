package com.onepiece.treasure.core.dao.basic

import com.onepiece.treasure.utils.JdbcBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet

abstract class BasicDao<T>(
        private val table: String
): BasicQueryDao<T> {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    abstract fun mapper(): (rs: ResultSet) -> T


    override fun get(id: Int): T {
        return JdbcBuilder.query(jdbcTemplate, table).where("id", id).executeOnlyOne(mapper())
    }

    override fun all(): List<T> {
        return JdbcBuilder.query(jdbcTemplate, table).execute(mapper())
    }

}