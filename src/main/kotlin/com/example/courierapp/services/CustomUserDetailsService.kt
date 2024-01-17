package com.example.courierapp.services

import com.example.courierapp.repositories.CustomerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias ApplicationUser = com.example.courierapp.entities.Customer
@Service
class CustomUserDetailsService(
    private val userRepository: CustomerRepository
): UserDetailsService {
    private val logger: Logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        try {
            val user = userRepository.findByEmail(username)
                ?.mapToUserDetails()
                ?: throw UsernameNotFoundException("UsernameNotFoundException: Not found!")

            logger.info("User details loaded successfully for username: {}", username)

            return user
        } catch (e: Exception) {
            logger.error("Error loading user details for username: {}", username, e)
            throw e
        }
    }


    private fun ApplicationUser.mapToUserDetails (): UserDetails =
        User.builder ()
            .username(this.email)
            .password(this.password)
            .roles(this.role.name)
            .build()

}