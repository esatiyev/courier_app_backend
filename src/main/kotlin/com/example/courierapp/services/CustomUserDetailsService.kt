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

    fun loadUserById(id: String): UserDetails {
        try {
            val customerById = userRepository.findById(id.toLong())
            if (!customerById.isPresent || customerById.get().email == null) {
                throw UsernameNotFoundException("UsernameNotFoundException: Not found!")
            }
            val user = customerById.get().mapToUserDetails()

            logger.info("User details loaded successfully for user ID: {}", id)

            return user
        } catch (e: UsernameNotFoundException) {
            logger.error("Error loading user details for user ID: {}", id, e)
            throw e
        }
        catch (e: Exception) {
            logger.error("Error loading user details for user ID: {}", id, e)
            throw e
        }
    }


    private fun ApplicationUser.mapToUserDetails (): UserDetails =
        User.builder ()
//            .username(this.email)
            .username(this.id.toString())
            .password(this.password)
            .roles(this.role.name)
            .build()



}