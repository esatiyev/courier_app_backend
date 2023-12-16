//package com.example.courierapp.controllers
//
//import com.example.courierapp.entities.LoginRequest
//import com.example.courierapp.entities.LoginResponse
//import com.example.courierapp.services.AuthService
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//
//
//@RestController
//@RequestMapping("/api/auth")
//class AuthController(private val authService: AuthService) {
//
//    @PostMapping("/login")
//    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
//        val token = authService.login(loginRequest.email, loginRequest.password)
//        return ResponseEntity.ok(token)
//    }
//}
