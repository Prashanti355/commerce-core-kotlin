package com.commercecore.backend.shared.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class RequestLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()

        MDC.put("requestId", requestId)

        try {
            filterChain.doFilter(request, response)
        } finally {
            val durationMs = System.currentTimeMillis() - startTime
            val principal = SecurityContextHolder.getContext().authentication?.name ?: "anonymous"

            log.info(
                "request_id={} method={} path={} status={} duration_ms={} user={}",
                requestId,
                request.method,
                request.requestURI,
                response.status,
                durationMs,
                principal
            )

            MDC.clear()
        }
    }
}