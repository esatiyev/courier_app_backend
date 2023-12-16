//package com.example.courierapp.configs
//
//import io.jsonwebtoken.Jwts
//import io.jsonwebtoken.SignatureAlgorithm
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.stereotype.Component
//import java.util.*
//
//@Component
//class JwtUtil {
//    @Value("\${jwt.secret}")
//    private var secret: String? = null
//
//    fun generateToken(email: String): String {
//        if (secret.isNullOrBlank()) {
//            throw IllegalStateException("JWT secret is not initialized.")
//        }
//
//        val expiration = Date(System.currentTimeMillis() + 864_000_000) // 10 days
//        return Jwts.builder()
//            .setSubject(email)
//            .setExpiration(expiration)
//            .signWith(SignatureAlgorithm.HS512, secret)
//            .compact()
//    }
//
//    fun validateToken(token: String): Boolean {
//        return try {
//            Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
//            true
//        } catch (e: Exception) {
//            false
//        }
//    }
//}
