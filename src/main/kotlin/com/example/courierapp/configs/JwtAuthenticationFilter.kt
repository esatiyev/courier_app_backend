package com.example.courierapp.configs

import com.example.courierapp.exception.ExpiredJwtException
import com.example.courierapp.services.CustomUserDetailsService
import com.example.courierapp.services.TokenService
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private val CustomLogger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal (
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")

        if (authHeader.doesNotContainBearerToken()) {
            CustomLogger.debug("No Bearer token found in the Authorization header. Proceeding with the next filter.")
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader!!.extractTokenValue()
        val id: String?

        try {
            id = tokenService.extractId(jwtToken)

        } catch (e: io.jsonwebtoken.ExpiredJwtException){
            CustomLogger.error("Token is expired: {}", e.message)
            throw ExpiredJwtException("Token is expired!")
        } catch (e: SignatureException){
            CustomLogger.error("Token is invalid: {}", e.message)
            throw SignatureException("Token is invalid!")
        } catch (e: Exception){
            CustomLogger.error("Error extracting email from token: {}", e.message)
            throw e
        }

        if (id != null && SecurityContextHolder.getContext().authentication == null) {
            val foundUser = userDetailsService.loadUserById(id)

            if (tokenService.isValid(jwtToken, foundUser)) {
                updateContext(foundUser, request)
            } else {
                CustomLogger.warn("Invalid token for user: {}", id)
            }

            filterChain.doFilter(request, response)
        }

    }

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authToken

        // Update the log message
        CustomLogger.info("User ID {} authenticated. Security context updated.", foundUser.username)
    }

    private fun String?.doesNotContainBearerToken(): Boolean =
        (this == null) || !this.startsWith("Bearer ")

    private fun String.extractTokenValue(): String =
        this.substringAfter("Bearer ")
}