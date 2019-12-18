package com.onepiece.treasure.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.security.Key
import java.util.*


@Component
class JwtTokenUtil : Serializable {

    @Value("\${jwt.secret}")
    lateinit var secret: String

    @Value("\${jwt.expiration}")
    var expiration: Long = 1

    companion object {

        private const val serialVersionUID = -3301605591108950415L

        private val CLAIM_KEY_USERNAME = "sub"
        private val CLAIM_KEY_CREATED = "created"
    }


    internal var key: Key? = null

    private fun getKey(): Key {
        if (key == null) {
            key = Keys.hmacShaKeyFor(secret.toByteArray())
        }
        return key!!
    }


    fun getUsernameFromToken(token: String): String? {
        var username: String?
        try {
            val claims = getClaimsFromToken(token)
            username = claims.subject
        } catch (e: Exception) {
//            log.error("", e)
            username = null
        }

        return username
    }

    fun getCreatedDateFromToken(token: String): Date? {
        var created: Date?
        try {
            val claims = getClaimsFromToken(token)
            created = Date(claims[CLAIM_KEY_CREATED] as Long)
        } catch (e: Exception) {
            created = null
        }

        return created
    }

    fun getExpirationDateFromToken(token: String): Date? {
        var expiration: Date?
        try {
            val claims = getClaimsFromToken(token)
            expiration = claims.expiration
        } catch (e: Exception) {
            expiration = null
        }

        return expiration
    }

    private fun getClaimsFromToken(token: String): Claims {
        try {
            return Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token).body
        } catch (e: Exception) {
            throw e
        }

    }

    private fun generateExpirationDate(): Date {
        return Date(System.currentTimeMillis() + expiration * 1000)
    }

    private fun isTokenExpired(token: String): Boolean {
        //        final Date expiration = getExpirationDateFromToken(token);
        //        return expiration.before(new Date());
        return false
    }

    private fun isCreatedBeforeLastPasswordReset(created: Date?, lastPasswordReset: Date?): Boolean {
        //return lastPasswordReset != null && created!!.before(lastPasswordReset)
        return false
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        claims[CLAIM_KEY_USERNAME] = userDetails.username
        claims[CLAIM_KEY_CREATED] = Date()
        return generateToken(claims)
    }

    internal fun generateToken(claims: Map<String, Any>): String {

        return Jwts.builder()
                .addClaims(claims)
                .signWith(getKey())
                // TODO 设置过期时间
                //.setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000))
                //                .setExpiration(new Date(System.currentTimeMillis() - 7 * 24 * 3600 * 1000))
                .compact()
    }

    fun canTokenBeRefreshed(token: String, lastPasswordReset: Date): Boolean? {
        val created = getCreatedDateFromToken(token)
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset) && !isTokenExpired(token)
    }

    fun refreshToken(token: String): String? {
        var refreshedToken: String?
        try {
            val claims = getClaimsFromToken(token)
            claims[CLAIM_KEY_CREATED] = Date()
            refreshedToken = generateToken(claims)
        } catch (e: Exception) {
            refreshedToken = null
        }

        return refreshedToken
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val user = userDetails as JwtUser
        val username = getUsernameFromToken(token)
        val created = getCreatedDateFromToken(token)
        //        final Date expiration = getExpirationDateFromToken(token);
        return (username == user.username
                && !isTokenExpired(token)
                && !isCreatedBeforeLastPasswordReset(created, user.lastPasswordResetDate))
    }


}
