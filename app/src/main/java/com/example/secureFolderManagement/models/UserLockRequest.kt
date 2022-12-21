package com.example.secureFolderManagement.models

data class UserLockRequest (
    val username: String,
    val isLocked: Boolean
)