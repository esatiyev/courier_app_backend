package com.example.courierapp.services

import com.example.courierapp.repositories.CustomerRepository
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
    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByEmail(username)
            ?.mapToUserDetails()
            ?: throw UsernameNotFoundException("UsernameNotFoundException: Not found!")

    private fun ApplicationUser.mapToUserDetails (): UserDetails =
        User.builder ()
            .username(this.email)
            .password(this.password)
            .roles(this.role.name)
            .build()

}