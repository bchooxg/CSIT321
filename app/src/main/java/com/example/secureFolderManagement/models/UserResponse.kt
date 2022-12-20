package com.example.secureFolderManagement.models

data class UserResponse(
    val id: Int,
    val usergroup: String,
    val username: String,
    val minPass: Int,
    val requireBiometrics: Boolean,
    val requireEncryption: Boolean,
    val company_id: String,
    val pin_type: String,
    val pin_max_tries: Int,
    val pin_lockout_time: Int,
)