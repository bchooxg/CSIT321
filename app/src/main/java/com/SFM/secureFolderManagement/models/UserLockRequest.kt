package com.SFM.secureFolderManagement.models

data class UserLockRequest (
    val username: String,
    val isLocked: Boolean
)