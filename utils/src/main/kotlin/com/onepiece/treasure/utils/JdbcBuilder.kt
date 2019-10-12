package com.onepiece.treasure.utils

import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet


object JdbcBuilder {

    fun insert(jdbcTemplate: JdbcTemplate, table: String): Insert {
        return Insert(jdbcTemplate, table)
    }

    fun query(jdbcTemplate: JdbcTemplate, table: String, returnColumns: String? = null): Query {
        return Query(jdbcTemplate, table, returnColumns)
    }

    fun update(jdbcTemplate: JdbcTemplate, table: String): Update {
        return Update(jdbcTemplate, table)
    }

}

class Insert(
        private val jdbcTemplate: JdbcTemplate,
        private val table: String
) {
    private val columns = arrayListOf<String>()
    private val param = arrayListOf<Any>()

    fun set(k: String, v: Any?): Insert {
        if (v == null) return this

        columns.add(k)

        val value = if (v is Enum<*>) v.name else v
        param.add(value)
        return this
    }

    private fun build(): String {

        val names = columns.joinToString(separator = ",")
        val questions = (0 until columns.size).joinToString(separator = ","){ "?" }

        return "insert into `$table` ($names) values ($questions)"
    }

    fun execute(): Int {
        val sql = this.build()
        return jdbcTemplate.update(sql, *param.toTypedArray())
    }
}

class Query(
        private val jdbcTemplate: JdbcTemplate,
        private val table: String,
        private val returnColumns: String? = null
) {
    private val columns = arrayListOf<String>()
    private val param = arrayListOf<Any>()
    var orderBy: String? = null

    fun where(k: String, v: Any?): Query {
        if (v == null) return this

        columns.add("$k = ?")

        val value = if (v is Enum<*>) v.name else v
        param.add(value)
        return this
    }

    fun asWhere(k: String, v: Any?): Query {
        if (v == null) return this

        columns.add(k)

        val value = if (v is Enum<*>) v.name else v
        param.add(value)
        return this
    }

    fun sort(orderBy: String): Query {
        this.orderBy = orderBy
        return this
    }

    private fun build(): String {

        val names = columns.joinToString(separator = " and ")

        val queryColumn = returnColumns?: "*"

        val sql =  if (columns.isEmpty()) {
            "select $queryColumn from `$table`"
        } else {
            "select $queryColumn from `$table` where $names"
        }

        return if (orderBy.isNullOrBlank()) {
            sql
        } else {
            "$sql order by $orderBy"
        }

    }

    fun <T> execute(function: (rs: ResultSet) -> T): List<T> {
        val sql = this.build()
        return jdbcTemplate.query(sql, param.toTypedArray()) { rs, _ ->
            function(rs)
        }
    }

    fun <T> executeOnlyOne(function: (rs: ResultSet) -> T): T {
        return this.execute(function).first()
    }

    fun <T> executeMaybeOne(function: (rs: ResultSet) -> T): T? {
        return this.execute(function).firstOrNull()
    }

    fun count(): Int {
        val sql = this.build()
        return jdbcTemplate.queryForObject(sql, param.toTypedArray(), Int::class.java)
    }

}

class Update(
        private val jdbcTemplate: JdbcTemplate,
        private val table: String
) {
    private val columns = arrayListOf<String>()
    private val whereColumns = arrayListOf<String>()

    private val param = arrayListOf<Any>()

    fun set(k: String, v: Any?): Update {
        if (v == null) return this

        columns.add("$k = ?")

        val value = if (v is Enum<*>) v.name else v
        param.add(value)
        return this
    }

    fun asSet(k: String): Update {

        columns.add(k)

        return this
    }


    fun where(k: String, v: Any?): Update {
        if (v == null) return this

        whereColumns.add("$k = ?")

        val value = if (v is Enum<*>) v.name else v
        param.add(value)
        return this
    }


    fun asWhere(k: String, v: Any?): Update {
        if (v == null) return this

        whereColumns.add(k)

        val value = if (v is Enum<*>) v.name else v
        param.add(value)
        return this
    }

    private fun build(): String {

        val setColumns = columns.joinToString(separator = ", ")
        val whereColumns = whereColumns.joinToString(separator = " and ")

        return "update `$table` set $setColumns where $whereColumns"

    }

    fun execute(): Int {
        val sql = this.build()
        return jdbcTemplate.update(sql, *param.toTypedArray())
    }

}