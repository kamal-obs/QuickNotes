package com.quicknotes.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val company: CompanyDto
)

@Serializable
data class CompanyDto(
    val name: String
)
