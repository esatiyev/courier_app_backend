package com.example.courierapp.controllers.auth

data class AuthenticationResponse (
    val accessToken: String,
    val refreshToken: String
)