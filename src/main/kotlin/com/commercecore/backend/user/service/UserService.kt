package com.commercecore.backend.user.service

import com.commercecore.backend.user.api.v1.dto.ChangePasswordRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.CreateUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.PatchUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UpdateUserRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UpdateUserRoleRequestV1Dto
import com.commercecore.backend.user.api.v1.dto.UserResponseV1Dto

interface UserService {

    fun getAllUsers(): List<UserResponseV1Dto>

    fun getDeletedUsers(): List<UserResponseV1Dto>

    fun getUserById(id: Long): UserResponseV1Dto

    fun createUser(createUserRequestV1Dto: CreateUserRequestV1Dto): UserResponseV1Dto

    fun updateUser(id: Long, updateUserRequestV1Dto: UpdateUserRequestV1Dto): UserResponseV1Dto

    fun patchUser(id: Long, patchUserRequestV1Dto: PatchUserRequestV1Dto): UserResponseV1Dto

    fun changePassword(id: Long, changePasswordRequestV1Dto: ChangePasswordRequestV1Dto): UserResponseV1Dto

    fun updateUserRole(id: Long, updateUserRoleRequestV1Dto: UpdateUserRoleRequestV1Dto): UserResponseV1Dto

    fun deactivateUser(id: Long): UserResponseV1Dto

    fun activateUser(id: Long): UserResponseV1Dto

    fun deleteUser(id: Long): UserResponseV1Dto

    fun restoreUser(id: Long): UserResponseV1Dto
}