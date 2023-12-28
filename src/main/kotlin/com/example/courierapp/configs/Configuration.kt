package com.example.courierapp.configs

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation. Configuration

@Configuration
@EnableConfigurationProperties (JwtProperties::class)
class Configuration {
}