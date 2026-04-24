package com.commercecore.backend.auth.security

import com.commercecore.backend.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmailAndDeletedFalse(username)
            .orElseThrow {
                UsernameNotFoundException("Usuario no encontrado con email: $username")
            }

        if (!user.active) {
            throw UsernameNotFoundException("El usuario está inactivo")
        }

        return CustomUserDetails(user)
    }
}