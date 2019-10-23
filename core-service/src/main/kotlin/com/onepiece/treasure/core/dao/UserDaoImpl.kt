package com.onepiece.treasure.core.dao

import com.onepiece.treasure.core.model.User
import com.onepiece.treasure.utils.JdbcBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class UserDaoImpl(
        val jdbcTemplate: JdbcTemplate
) : UserDao  {

    override fun get(id: Int): User {

        return JdbcBuilder.query(jdbcTemplate, "user").where("id", id).executeOnlyOne {  rs ->
            val name = rs.getString("name")
            User(id = id, name = name)
        }
    }
}