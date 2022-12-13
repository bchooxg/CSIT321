package com.example.secureFolderManagement.models

data class UserResponse(
    val id: Int,
    val password: String,
    val usergroup: String,
    val username: String
)