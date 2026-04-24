package com.commercecore.backend.auth.security

import com.commercecore.backend.auth.service.JwtService
import com.commercecore.backend.auth.service.TokenBlacklistService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsServiceImpl: UserDetailsServiceImpl,
    private val tokenBlacklistService: TokenBlacklistService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)

        try {
            val username = jwtService.extractUsername(token)
            val jti = jwtService.extractJti(token)
            val tokenType = jwtService.extractType(token)

            if (tokenType != "access" || tokenBlacklistService.isBlacklisted(jti)) {
                filterChain.doFilter(request, response)
                return
            }

            if (username.isNotBlank() && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = userDetailsServiceImpl.loadUserByUsername(username) as CustomUserDetails

                if (jwtService.isTokenValid(token, userDetails, "access")) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        } catch (_: Exception) {
        }

        filterChain.doFilter(request, response)
    }
}