package com.onepiece.treasure.account.dao

import com.onepiece.treasure.account.model.User
import com.onepiece.treasure.utils.JdbcBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserDaoImpl(
        val jdbcTemplate: JdbcTemplate
) : UserDao  {

    override fun get(id: Int): User {

        val sql = "select * from user where id = ?"
        return JdbcBuilder.query(jdbcTemplate, sql).executeOnlyOne {  rs ->
            val name = rs.getString("name")
            User(id = id, name = name)
        }
    }
}