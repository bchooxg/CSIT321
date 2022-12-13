package com.example.secureFolderManagement.models

data class UserRequest(
    val username: String,
    val password: String,
    val fromApp: Boolean
)