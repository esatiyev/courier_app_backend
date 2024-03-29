package com.example.courierapp.services

import com.example.courierapp.configs.JwtProperties
import com.example.courierapp.controllers.auth.AuthenticationRequest
import com.example.courierapp.controllers.auth.AuthenticationResponse
import com.example.courierapp.repositories.RefreshTokenRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    private val logger: Logger = LoggerFactory.getLogger(AuthenticationService::class.java)

    fun authentication(authRequest: AuthenticationRequest): AuthenticationResponse {
        val accessToken: String
        val refreshToken: String
        try {
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authRequest.email,
                    authRequest.password
                )
            )

            val user = userDetailsService.loadUserByUsername(authRequest.email)

            accessToken = generateAccessToken(user)

            refreshToken = generateRefreshToken(user)

            refreshTokenRepository.save(refreshToken, user)

            logger.info("Authentication successful for user: {}", authRequest.email)

            return AuthenticationResponse(
                accessToken = accessToken,
                refreshToken = refreshToken
            )

        } catch (e: AuthenticationException) {
            logger.error("Authentication failed for user: {}", authRequest.email, e)
            throw e
        } catch (e: Exception) {
            logger.error("Authentication failed for user: {}", authRequest.email, e)
            throw e
        }


    }

    fun refreshAccessToken(token: String): String? {
        val extractedEmail = tokenService.extractId(token)

        return extractedEmail?.let { email ->
            val currentUserDetails = userDetailsService.loadUserByUsername(email)
            val refreshTokenUserDetails = refreshTokenRepository.findUserDetailsByToken(token)

            if (!tokenService.isExpired(token) && currentUserDetails.username == refreshTokenUserDetails?.username)
                generateAccessToken(currentUserDetails)
            else
                null
        }

    }

    private fun generateAccessToken(user: UserDetails) = tokenService.generate(
        userId = user.username,
        userDetails = user,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
    )

    private fun generateRefreshToken(user: UserDetails) = tokenService.generate(
        userId = user.username,
        userDetails = user,
        expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
    )

}
