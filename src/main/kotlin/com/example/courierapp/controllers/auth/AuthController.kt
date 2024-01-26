package com.example.courierapp.controllers.auth

import com.example.courierapp.repositories.CustomerRepository
import com.example.courierapp.services.AuthenticationService
import com.example.courierapp.services.CustomerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping ("/api/auth")
class AuthController (
    private val authenticationService: AuthenticationService,
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun authenticate (@RequestBody authRequest: AuthenticationRequest) : AuthenticationResponse {
        logger.info("Received authentication request: {}", authRequest.email)
        return authenticationService.authentication(authRequest)
    }


    @PostMapping ("/refresh")
    fun refreshAccessToken(
        @RequestBody request: RefreshTokenRequest
    ): TokenResponse {
        // Log refresh token request
        logger.info("Received refresh token request: {}", request)

        val refreshedToken = authenticationService.refreshAccessToken(request.token)
        return refreshedToken?.mapToTokenResponse()
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token!")
    }


    private fun String.mapToTokenResponse(): TokenResponse =
        TokenResponse(
            token = this
        )

}