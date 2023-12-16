//package com.example.courierapp.configs
//
//
//import com.example.courierapp.services.AuthService
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//import org.springframework.security.crypto.password.PasswordEncoder
//
//@Configuration
//@EnableWebSecurity
//class SecurityConfig(private val authService: AuthService): WebSecurityConfigurerAdapter() {
//
//    @Bean
//    fun passwordEncoder(): PasswordEncoder {
//        return BCryptPasswordEncoder()
//    }
//
//    @Throws(Exception::class)
//    override fun configure(auth: AuthenticationManagerBuilder) {
//        auth.userDetailsService(authService).passwordEncoder(passwordEncoder())
//    }
//
//    @Throws(Exception::class)
//    override fun configure(http: HttpSecurity) {
//        http
//            .csrf().disable()
//            .authorizeRequests()
//            .antMatchers("/api/**").authenticated()
//            .and()
//            .httpBasic()
//    }
//}
