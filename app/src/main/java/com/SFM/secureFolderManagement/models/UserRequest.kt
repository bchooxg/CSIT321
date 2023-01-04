package com.SFM.secureFolderManagement.models

data class UserRequest(
    val username: String,
    val password: String,
    val fromApp: Boolean
)