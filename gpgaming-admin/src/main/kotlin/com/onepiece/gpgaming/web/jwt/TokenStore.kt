package com.onepiece.gpgaming.web.jwt

import com.onepiece.gpgaming.utils.RedisService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class TokenStore(
        val tokenRedisTemplate: RedisTemplate<String, Any>,
        val redisService: RedisService
) {

    @Value("\${jwt.expiration}")
    var expiration: Int = 0


    fun storeAccessToken(username: String, token: String, jwtUser: JwtUser) {

        this.removeAccessToken(username)

//        tokenRedisTemplate.opsForValue().set("access:$username", token, expiration.toLong(), TimeUnit.DAYS)
//        tokenRedisTemplate.opsForValue().set(token, jwtUser, expiration.toLong(), TimeUnit.DAYS)
        redisService.put("access:$username", token)
        redisService.put(token, jwtUser)
    }

    fun readAccessToken(token: String): JwtUser? {
        return redisService.get(token, JwtUser::class.java)
    }

    fun refresh(jwtUser: JwtUser): Boolean {
        val accessToken = redisService.get("access:${jwtUser.username}", String::class.java) ?: return false
        redisService.put(accessToken, jwtUser)
        return true
    }


    fun removeAccessToken(username: String) {
        val usernameAccessKey = "access:$username"
//        val token = tokenRedisTemplate.opsForValue().get(usernameAccessKey) as String?

        val token = redisService.get(usernameAccessKey, String::class.java)

        tokenRedisTemplate.delete(usernameAccessKey)
        if (token != null) {
            tokenRedisTemplate.delete(token)
        }
    }

}