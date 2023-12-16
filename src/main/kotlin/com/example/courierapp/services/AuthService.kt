//package com.example.courierapp.services
//
//import com.example.courierapp.entities.Customer
//import com.example.courierapp.entities.LoginResponse
//import com.example.courierapp.repositories.CustomerRepository
//import io.jsonwebtoken.Jwts
//import io.jsonwebtoken.SignatureAlgorithm
//import org.springframework.security.authentication.BadCredentialsException
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.core.userdetails.User
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.core.userdetails.UsernameNotFoundException
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
//import org.springframework.stereotype.Service
//import java.nio.file.attribute.UserPrincipal
//import java.util.*
//
//@Service
//class AuthService(
//    private val customerRepository: CustomerRepository,
//    private val passwordEncoder: PasswordEncoder
//): UserDetailsService {
//
//    fun login(email: String, password: String): LoginResponse {
//        val customer = customerRepository.findByEmail(email)
//            ?: throw UsernameNotFoundException("User not found")
//
//        if (passwordEncoder.matches(password, customer.password)) {
//            // Generate JWT token
//            val token = generateJwtToken(customer)
//            return LoginResponse(token)
//        } else {
//            throw BadCredentialsException("Invalid password")
//        }
//    }
//
//    private fun generateJwtToken(customer: Customer): String {
//        val expiration = Date(System.currentTimeMillis() + 86400000) // 1 day
//        return Jwts.builder()
//            .setSubject(customer.email)
//            .setIssuedAt(Date())
//            .setExpiration(expiration)
//            .signWith(SignatureAlgorithm.HS512, "your-secret-key") // Replace with your secret key
//            .compact()
//    }
//
//    @Throws(Exception::class)
//    protected fun configure(http: HttpSecurity) {
//        http.csrf().disable()
//            .authorizeRequests()
//            .antMatchers("/api/auth/**").permitAll()
//            .anyRequest().authenticated()
//            .and().httpBasic()
//            .and().addFilterBefore(JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
//    }
//
//    // Implement the loadUserByUsername method
//    override fun loadUserByUsername(email: String): UserDetails {
//        val customer = customerRepository.findByEmail(email)
//            ?: throw UsernameNotFoundException("User not found with email: $email")
//
//        return User(customer.email, customer.password, emptyList())
//    }
//
//}
//
