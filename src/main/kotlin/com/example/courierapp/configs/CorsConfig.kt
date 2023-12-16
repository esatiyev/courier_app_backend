//package com.example.courierapp.configs
//
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.web.cors.CorsConfiguration
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource
//import org.springframework.web.filter.CorsFilter
//
//@Configuration
//class CorsConfig {
//
//    @Bean
//    fun corsFilter(): CorsFilter {
//        val source = UrlBasedCorsConfigurationSource()
//        val config = CorsConfiguration()
//
//        // Allow all origins, methods, and headers. This is just an example.
//        config.addAllowedOrigin("*")
//        config.addAllowedMethod("*")
//        config.addAllowedHeader("*")
//        config.allowCredentials = true
//
//        source.registerCorsConfiguration("/api/**", config)
//
//        return CorsFilter(source)
//    }
//}
