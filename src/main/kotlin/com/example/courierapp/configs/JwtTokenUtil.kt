/*
package com.example.courierapp.configs

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {

    private val SECRET_KEY = "your-secret-key"

    fun generateToken(username: String): String {
        val now = Date()
        val expiryDate = Date(now.time + 86400000) // Token expires in 24 hours

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact()
    }

    fun getUsernameFromToken(token: String): String {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body.subject
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun isTokenExpired(token: String): Boolean {
        val expirationDate = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).body.expiration
        return expirationDate.before(Date())
    }
}
*/
