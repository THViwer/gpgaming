package com.onepiece.treasure.utils

import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import java.sql.PreparedStatement
import java.sql.ResultSet


object JdbcBuilder {

    fun insert(jdbcTemplate: JdbcTemplate, table: String): Insert {
        return Insert(jdbcTemplate, table)
    }

    fun <T> batchInsert(jdbcTemplate: JdbcTemplate, table: String, data: List<T>): BatchInsert<T> {
        return BatchInsert(jdbcTemplate, table, data)
    }

    fun query(jdbcTemplate: JdbcTemplate, table: String, returnColumns: String? = null): Query {
        return Query(jdbcTemplate, table, returnColumns)
    }

    fun update(jdbcTemplate: JdbcTemplate, table: String): Update {
        return Update(jdbcTemplate, table)
    }

}

class BatchInsert<T>(
        private val jdbcTemplate: JdbcTemplate,
        private val table: String,
        private val data: List<T>
) {


    private val columns = arrayListOf<String>()
//    val fs = arrayListOf<(ps: PreparedStatement, entity: T) -> Unit>()


    fun set(column: String): BatchInsert<T> {
        columns.add("`$column`")
        return this
    }


//    fun set(column: String, f1: (ps: PreparedStatement, entity: T) -> Unit): BatchInsert<T> {
//        columns.add("`$column`")
//        fs.add(f1)
//        return this
//    }


    fun execute(function: (ps: PreparedStatement, entity: T) -> Any?) {
        val batch = object: BatchPreparedStatementSetter{
            override fun setValues(ps: PreparedStatement, i: Int) {
                function(ps, data[i])
            }

            override fun getBatchSize(): Int {
                return data.size
            }
        }

        val names = columns.joinToString(separator = ",")
        val values = columns.joinToString(separator = ",") { "?" }
        val sql = "insert ignore into `$table` ($names) values ($values)"

        jdbcTemplate.batchUpdate(sql, batch)
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


    fun build(): String {

        val names = columns.joinToString(separator = ",")
        val questions = (0 until columns.size).joinToString(separator = ","){ "?" }

        return "insert ignore into `$table` ($names) values ($questions)"
    }

    fun execute(): Int {
        val sql = this.build()
        return jdbcTemplate.update(sql, *param.toTypedArray())
    }

    fun executeGeneratedKey(): Int {
        val map = (0 until columns.size).mapIndexed { index, _ ->
            columns[index] to param[index]
        }.toMap()

        return SimpleJdbcInsert(jdbcTemplate)
                .withTableName(table)
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKey(map)
                .toInt()
    }

    fun executeOnlyOne(): Boolean {
        return execute() == 1
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
    var groupBy: String? = null
    var current: Int? = null
    var size: Int? = null

    fun where(k: String, v: Any?): Query {
        if (v == null) return this

        columns.add("`$k` = ?")

        val value = if (v is Enum<*>) v.name else v
        param.add(value)
        return this
    }

    fun whereIn(k: String, vs: List<Any>?): Query {
        if (vs == null) return this

        columns.add("`$k` in (${vs.joinToString(separator = ",")})")

        return this
    }

    fun asWhere(k: String, v: Any?): Query {
        if (v == null) return this

        columns.add(k)

        val value = if (v is Enum<*>) v.name else v
        param.add(value)
        return this
    }

    fun group(groupBy: String): Query {
        this.groupBy = groupBy
        return this
    }

    fun sort(orderBy: String): Query {
        this.orderBy = orderBy
        return this
    }

    fun limit(current: Int, size: Int): Query {
        this.current = current
        this.size = size
        return this
    }


    private fun build(): String {

        val names = columns.joinToString(separator = " and ")

        val queryColumn = returnColumns?: "*"

        val begin =  if (columns.isEmpty()) {
            "select $queryColumn from `$table`"
        } else {
            "select $queryColumn from `$table` where $names"
        }

        val sql = StringBuilder(begin)

        if (!groupBy.isNullOrBlank()) {
            sql.append(" group by $groupBy ")
        }

        if (!orderBy.isNullOrBlank()) {
            sql.append(" order by $orderBy")
        }

        if (current != null && size != null) {
            sql.append(" limit $current, $size")
        }

        return sql.toString()
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

    fun asWhere(k: String): Update {

        whereColumns.add(k)

        return this
    }


    fun asWhere(k: String, v: Any?): Update {
        if (v == null) return this

        whereColumns.add(k)

        val value = if (v is Enum<*>) v.name else v
        param.add(value)
        return this
    }

    fun build(): String {

        val setColumns = columns.joinToString(separator = ", ")
        val whereColumns = whereColumns.joinToString(separator = " and ")

        return "update `$table` set $setColumns where $whereColumns"

    }

    fun execute(): Int {
        val sql = this.build()
        return jdbcTemplate.update(sql, *param.toTypedArray())
    }

    fun executeOnlyOne(): Boolean {
        return execute() == 1
    }

}