/*
package com.example.courierapp.services

import com.example.courierapp.entities.Customer
import com.example.courierapp.repositories.CustomerRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class MyUserDetailsServiceImpl(private val customerRepository: CustomerRepository) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val customer = customerRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")

        return User(
            customer.email,
            customer.password,
            listOf(getRoleForCustomerOrCourier(customer))
        )
    }

    private fun getRoleForCustomerOrCourier(customer: Customer): GrantedAuthority {
        return if (customer.isCourier == true) {
            SimpleGrantedAuthority("ROLE_COURIER")
        } else {
            SimpleGrantedAuthority("ROLE_CUSTOMER")
        }
    }

}*/
