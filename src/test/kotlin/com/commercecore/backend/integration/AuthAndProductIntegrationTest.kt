package com.commercecore.backend.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.util.UUID
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authorization.AuthorizationDeniedException
@Testcontainers
@AutoConfigureTestRestTemplate
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "app.jwt.secret=8f1b5d3c7a9e2f4b6c8d0e1f3a5b7c9d2e4f6a8b0c1d3e5f7a9b2c4d6e8f0a1",
        "app.jwt.access-token-expiration-ms=900000",
        "app.jwt.refresh-token-expiration-ms=604800000",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.show-sql=false",
        "logging.level.org.hibernate.SQL=WARN",
        "logging.level.org.springframework.security=WARN"
    ]
)
class AuthAndProductIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    companion object {

        @Container
        @ServiceConnection
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16")
    }

    @Test
    fun `debe registrar usuario y permitir login`() {
        val email = uniqueEmail("user")
        val password = "Password123"

        createUser(email, password)

        val accessToken = login(email, password)

        assertThat(accessToken).isNotBlank()
    }

    @Test
    fun `usuario normal no debe crear producto`() {
        val email = uniqueEmail("normal")
        val password = "Password123"

        createUser(email, password)
        val accessToken = login(email, password)

        val response = restTemplate.exchange(
            "/api/v1/products",
            HttpMethod.POST,
            HttpEntity(createProductBody(), authHeaders(accessToken)),
            String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `admin debe crear producto y registrar auditoria`() {
        val email = uniqueEmail("admin")
        val password = "Admin1234"

        createUser(email, password)
        promoteToAdmin(email)

        val accessToken = login(email, password)

        val response = restTemplate.exchange(
            "/api/v1/products",
            HttpMethod.POST,
            HttpEntity(createProductBody(), authHeaders(accessToken)),
            String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)

        val auditCount = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM audit_logs
            WHERE actor_email = ?
              AND action = 'PRODUCT_CREATE'
              AND entity_type = 'PRODUCT'
            """.trimIndent(),
            Long::class.java,
            email
        )

        assertThat(auditCount).isNotNull
        assertThat(auditCount!!).isGreaterThanOrEqualTo(1)
    }

    @Test
    fun `admin debe consultar productos eliminados`() {
        val email = uniqueEmail("admin-deleted")
        val password = "Admin1234"

        createUser(email, password)
        promoteToAdmin(email)

        val accessToken = login(email, password)

        val response = restTemplate.exchange(
            "/api/v1/products/deleted?page=0&size=5",
            HttpMethod.GET,
            HttpEntity(null, authHeaders(accessToken)),
            String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).contains("\"code\":200")
        assertThat(response.body).contains("\"content\"")
    }

    private fun createUser(email: String, password: String) {
        val body = mapOf(
            "name" to "Integration User",
            "email" to email,
            "password" to password
        )

        val response = restTemplate.postForEntity(
            "/api/v1/users",
            HttpEntity(body, jsonHeaders()),
            String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    private fun login(email: String, password: String): String {
        val body = mapOf(
            "email" to email,
            "password" to password
        )

        val response = restTemplate.postForEntity(
            "/api/v1/auth/login",
            HttpEntity(body, jsonHeaders()),
            String::class.java
        )

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val responseBody = response.body ?: error("La respuesta de login vino vacía")

        val regex = Regex("\"accessToken\"\\s*:\\s*\"([^\"]+)\"")
        val match = regex.find(responseBody)
            ?: error("No se encontró accessToken en la respuesta: $responseBody")

        return match.groupValues[1]
    }

    private fun promoteToAdmin(email: String) {
        val updatedRows = jdbcTemplate.update(
            "UPDATE users SET role = 'ROLE_ADMIN' WHERE email = ?",
            email
        )

        assertThat(updatedRows).isEqualTo(1)
    }

    private fun createProductBody(): Map<String, Any?> {
        val sku = "SKU-${UUID.randomUUID()}"

        return mapOf(
            "name" to "Producto integración",
            "description" to "Producto creado desde prueba de integración",
            "price" to BigDecimal("499.99"),
            "stock" to 8,
            "sku" to sku,
            "imageUrl" to "https://example.com/producto.jpg"
        )
    }

    private fun jsonHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        return headers
    }

    private fun authHeaders(accessToken: String): HttpHeaders {
        val headers = jsonHeaders()
        headers.setBearerAuth(accessToken)
        return headers
    }

    private fun uniqueEmail(prefix: String): String {
        return "$prefix-${UUID.randomUUID()}@example.com"
    }
}