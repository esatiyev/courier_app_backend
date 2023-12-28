/*
package com.example.courierapp.configs

import com.example.courierapp.services.MyUserDetailsServiceImpl
import jakarta.servlet.Filter
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

import org.springframework.context.annotation.Configuration
import org.springframework.security.access.AccessDecisionManager
import org.springframework.security.access.SecurityMetadataSource
import org.springframework.security.access.vote.AffirmativeBased
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.access.intercept.DefaultFilterInvocationSecurityMetadataSource
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(private val userDetailsService: UserDetailsServiceImpl) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests {
                it.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                it.requestMatchers(RequestMatcher { request ->
                    // Add your custom request matching logic here
                    request.method == "GET" && request.pathInfo == "/authenticate"
                }).permitAll()
                it.anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(JwtRequestFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }
    override fun getFilters(): MutableList<Filter> {
        return mutableListOf()
    }

    override fun matches(request: HttpServletRequest): Boolean {
        return true
    }

    override fun getOrder(): Int {
        return 0
    }

    override fun getSecurityMetadataSource(): SecurityMetadataSource {
        return DefaultFilterInvocationSecurityMetadataSource(
            AntPathRequestMatcher("/**"),
            SecurityConfigAttributes.EMPTY
        )
    }

    override fun getAccessDecisionManager(): AccessDecisionManager {
        return AffirmativeBased(listOf())
    }

    override fun getAuthenticationManager(): AuthenticationManager {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userDetailsService)
        return provider.authenticationManager
    }
}
*/