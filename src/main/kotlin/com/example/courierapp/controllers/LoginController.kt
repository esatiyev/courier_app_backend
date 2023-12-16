package com.example.courierapp.controllers

//import com.example.courierapp.configs.JwtUtil
import com.example.courierapp.entities.LoginRequest
import com.example.courierapp.repositories.CustomerRepository
//import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/login")
class LoginController(private val customerRepository: CustomerRepository) {

    data class LoginResponse(val message: String, val customerId: Long?)

    @PostMapping
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        println("Received login request: $loginRequest")

        return try {
            val customer = customerRepository.findCustomerByEmailAndPassword(
                loginRequest.email,
                loginRequest.password
            )
            val response = LoginResponse("Login successful for Customer with id: ${customer.id}", customer.id)
            ResponseEntity.ok(response)

        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse("Exception: ${e.message}", -1))
        }
    }
}
