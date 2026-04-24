package com.commercecore.backend.user.api.v1.controller

import com.commercecore.backend.shared.util.ApiPaths
import com.commercecore.backend.shared.util.ResponseUtil
import com.commercecore.backend.user.api.v1.dto.CreateUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UpdateUserRequestV1Dto
import com.commercecore.backend.user.service.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiPaths.USERS_V1)
class UserControllerV1(
    private val userService: UserService
) {

    @GetMapping
    fun getAll() = ResponseUtil.success(
        message = "Lista de usuarios",
        data = userService.getAllUsers()
    )

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = ResponseUtil.success(
        message = "Usuario encontrado",
        data = userService.getUserById(id)
    )

    @PostMapping
    fun create(@Valid @RequestBody createUserRequestV1Dto: CreateUserRequestV1Dto) =
        ResponseUtil.created(
            message = "Usuario creado",
            data = userService.createUser(createUserRequestV1Dto)
        )

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody updateUserRequestV1Dto: UpdateUserRequestV1Dto
    ) = ResponseUtil.success(
        message = "Usuario actualizado",
        data = userService.updateUser(id, updateUserRequestV1Dto)
    )

    @PatchMapping("/{id}/deactivate")
    fun deactivate(@PathVariable id: Long) = ResponseUtil.success(
        message = "Usuario desactivado",
        data = userService.deactivateUser(id)
    )

    @PatchMapping("/{id}/activate")
    fun activate(@PathVariable id: Long) = ResponseUtil.success(
        message = "Usuario activado",
        data = userService.activateUser(id)
    )

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = ResponseUtil.success(
        message = "Usuario eliminado lógicamente",
        data = userService.deleteUser(id)
    )

    @PatchMapping("/{id}/restore")
    fun restore(@PathVariable id: Long) = ResponseUtil.success(
        message = "Usuario restaurado",
        data = userService.restoreUser(id)
    )

    @GetMapping("/ping")
    fun ping() = ResponseUtil.success(
        message = "Módulo de usuarios operativo",
        data = mapOf(
            "module" to "user",
            "version" to "v1"
        )
    )
}