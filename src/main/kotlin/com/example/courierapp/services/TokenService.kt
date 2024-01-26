package com.example.courierapp.services

import com.example.courierapp.configs.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenService (
    jwtProperties: JwtProperties
) {
    private val logger: Logger = LoggerFactory.getLogger(TokenService::class.java)

    private val secretKey = Keys.hmacShaKeyFor(
        jwtProperties.key.toByteArray()
    )

    fun generate(
        userId: String, // user id
        userDetails: UserDetails,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap()
    ): String {
        val token = Jwts.builder()
            .claims()
//            .subject(userDetails.username)
            .subject(userId)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .add(additionalClaims)
            .and()
            .signWith(secretKey)
            .compact()

        logger.info("Generated token for user ID: {}", userId /*userDetails.username*/)

        return token
    }


    fun extractId(token: String): String? =
        getAllClaims (token)
            .subject
    fun isExpired(token: String): Boolean =
        getAllClaims (token)
            .expiration
            .before (Date(System.currentTimeMillis()))

    fun isValid(token: String, userDetails: UserDetails): Boolean {
        val id = extractId(token)

        return userDetails.username == id && !isExpired(token)
    }

    private fun getAllClaims (token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(secretKey)
            .build()

        return parser
            .parseSignedClaims (token)
            .payload
    }
}