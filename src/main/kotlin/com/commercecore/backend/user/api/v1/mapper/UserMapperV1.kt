package com.commercecore.backend.user.api.v1.mapper

import com.commercecore.backend.user.api.v1.dto.UserResponseV1Dto
import com.commercecore.backend.user.entity.User

object UserMapperV1 {

    fun toResponseDto(user: User): UserResponseV1Dto {
        return UserResponseV1Dto(
            id = user.id,
            name = user.name,
            email = user.email,
            active = user.active,
            deleted = user.deleted,
            deletedAt = user.deletedAt,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}