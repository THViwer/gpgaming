package com.onepiece.treasure.web.jwt

import com.fasterxml.jackson.annotation.JsonIgnore
import com.onepiece.treasure.beans.enums.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class JwtUser(
        val id: Int,

        val clientId: Int,

        val role: Role,

        val musername: String,

        val mpassword: String,

        val lastPasswordResetDate: Date,

        val mAuthorities: List<String>


) : UserDetails {

    @JsonIgnore
    override fun getAuthorities(): List<GrantedAuthority> {
        return mAuthorities.map { GrantedAuthority { it } }
    }

    @JsonIgnore
    override fun getUsername(): String {
        return musername
    }

    @JsonIgnore
    override fun getPassword(): String {
        return mpassword
    }

    @JsonIgnore
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    @JsonIgnore
    override fun isEnabled(): Boolean {
        return true
    }


//    // 是否是会员
//    val member: Boolean
//        @JsonIgnore
//        get() {
//            return memberEndTime != null && memberEndTime.isAfter(LocalDateTime.now())
//        }
//
//    // 会员剩余多少天
//    val lastDay: Int
//        @JsonIgnore
//        get() {
//            memberEndTime ?: return  -1
//            return ChronoUnit.DAYS.between(LocalDate.now(), memberEndTime.toLocalDate()).toInt()
//        }

}