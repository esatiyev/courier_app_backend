package com.example.courierapp.configs

import com.example.courierapp.exception.ExpiredJwtException
import com.example.courierapp.services.CustomUserDetailsService
import com.example.courierapp.services.TokenService
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal (
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")

        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader!!.extractTokenValue()
        val email: String?
//        try {
        try {
            email = tokenService.extractEmail(jwtToken)

        } catch (e: io.jsonwebtoken.ExpiredJwtException){
            throw ExpiredJwtException("Token is expired!")
        } catch (e: SignatureException){
            throw ExpiredJwtException("Token is invalid!")
        } catch (e: Exception){
            throw ExpiredJwtException("Token is wrong!")
        }

            if (email != null && SecurityContextHolder.getContext().authentication == null) {
                val foundUser = userDetailsService.loadUserByUsername(email)

                if (tokenService.isValid(jwtToken, foundUser)) {
                    updateContext(foundUser, request)
                }

                filterChain.doFilter(request, response)
            }
//        } catch (e: io.jsonwebtoken.ExpiredJwtException) {
//            return
//        }

    }

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authToken
    }

    private fun String?.doesNotContainBearerToken(): Boolean =
        (this == null) || !this.startsWith("Bearer ")

    private fun String.extractTokenValue(): String =
        this.substringAfter("Bearer ")
}