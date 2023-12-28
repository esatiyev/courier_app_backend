/*
package com.example.courierapp.configs

//import com.example.courierapp.services.MyUserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfiguration(
//    private val myUserDetailsServiceImpl: MyUserDetailsServiceImpl,
) {


    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }







    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeRequests {
                authorize("/admin.html", permitAll)
                authorize("/api/login", permitAll)
                authorize(anyRequest, authenticated)
            }
            formLogin {
//                loginPage = "/login"
//                failureUrl = "/login?error"
            }
            logout {
//                logoutUrl = "/logout"
//                logoutSuccessUrl = "/login?logout"
            }
        }
        return http.build()
    }




    @Bean
    @Throws(Exception::class)
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        return http.getSharedObject(AuthenticationManagerBuilder::class.java)
            .build()
    }



    @Throws(java.lang.Exception::class)
    fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(myUserDetailsServiceImpl)
            .passwordEncoder(passwordEncoder())
    }


}*/
