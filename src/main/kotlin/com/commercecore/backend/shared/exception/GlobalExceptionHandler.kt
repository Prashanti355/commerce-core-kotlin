package com.commercecore.backend.shared.exception

import com.commercecore.backend.shared.util.ResponseUtil
import com.commercecore.backend.user.exception.InvalidCurrentPasswordException
import com.commercecore.backend.user.exception.UserConflictException
import com.commercecore.backend.user.exception.UserDeletedException
import com.commercecore.backend.user.exception.UserInactiveException
import com.commercecore.backend.user.exception.UserNotDeletedException
import com.commercecore.backend.user.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import com.commercecore.backend.user.exception.InvalidPatchRequestException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import com.commercecore.backend.product.exception.InvalidPatchProductRequestException
import com.commercecore.backend.product.exception.ProductConflictException
import com.commercecore.backend.product.exception.ProductDeletedException
import com.commercecore.backend.product.exception.ProductNotDeletedException
import com.commercecore.backend.product.exception.ProductNotFoundException
import com.commercecore.backend.user.exception.InvalidRoleException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authorization.AuthorizationDeniedException
import com.commercecore.backend.audit.exception.AuditLogNotFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException) =
        ResponseUtil.error(HttpStatus.NOT_FOUND, ex.message ?: "Usuario no encontrado")

    @ExceptionHandler(UserConflictException::class)
    fun handleUserConflictException(ex: UserConflictException) =
        ResponseUtil.error(HttpStatus.CONFLICT, ex.message ?: "Conflicto de datos")

    @ExceptionHandler(UserInactiveException::class)
    fun handleUserInactiveException(ex: UserInactiveException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Usuario inactivo")

    @ExceptionHandler(UserDeletedException::class)
    fun handleUserDeletedException(ex: UserDeletedException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Usuario ya eliminado")

    @ExceptionHandler(UserNotDeletedException::class)
    fun handleUserNotDeletedException(ex: UserNotDeletedException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Usuario no eliminado")

    @ExceptionHandler(InvalidCurrentPasswordException::class)
    fun handleInvalidCurrentPasswordException(ex: InvalidCurrentPasswordException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "La contraseña actual no es correcta")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException) =
        ResponseUtil.error(
            HttpStatus.BAD_REQUEST,
            ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseException(ex: HttpMessageNotReadableException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, "JSON inválido o mal formado")

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception) =
        ResponseUtil.error(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.message ?: "Error interno del servidor"
        )
    
    @ExceptionHandler(InvalidPatchRequestException::class)
    fun handleInvalidPatchRequestException(ex: InvalidPatchRequestException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Solicitud PATCH inválida")

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException) =
        ResponseUtil.error(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException) =
        ResponseUtil.error(HttpStatus.UNAUTHORIZED, ex.message ?: "No autenticado")

    @ExceptionHandler(ProductNotFoundException::class)
    fun handleProductNotFoundException(ex: ProductNotFoundException) =
        ResponseUtil.error(HttpStatus.NOT_FOUND, ex.message ?: "Producto no encontrado")

    @ExceptionHandler(InvalidPatchProductRequestException::class)
    fun handleInvalidPatchProductRequestException(ex: InvalidPatchProductRequestException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Solicitud PATCH de producto inválida")

    @ExceptionHandler(ProductConflictException::class)
    fun handleProductConflictException(ex: ProductConflictException) =
        ResponseUtil.error(HttpStatus.CONFLICT, ex.message ?: "Conflicto de datos en el producto")

    @ExceptionHandler(ProductDeletedException::class)
    fun handleProductDeletedException(ex: ProductDeletedException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Producto ya eliminado")

    @ExceptionHandler(ProductNotDeletedException::class)
    fun handleProductNotDeletedException(ex: ProductNotDeletedException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Producto no eliminado")

    @ExceptionHandler(InvalidRoleException::class)
    fun handleInvalidRoleException(ex: InvalidRoleException) =
        ResponseUtil.error(HttpStatus.BAD_REQUEST, ex.message ?: "Rol inválido")        

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(ex: AuthorizationDeniedException) =
        ResponseUtil.error(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción")

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException) =
        ResponseUtil.error(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción")        

    @ExceptionHandler(AuditLogNotFoundException::class)
    fun handleAuditLogNotFoundException(ex: AuditLogNotFoundException) =
        ResponseUtil.error(HttpStatus.NOT_FOUND, ex.message ?: "Registro de auditoría no encontrado")
}