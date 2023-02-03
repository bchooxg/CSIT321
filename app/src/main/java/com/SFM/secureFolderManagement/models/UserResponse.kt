package com.SFM.secureFolderManagement.models

data class UserResponse(
    val id: Int,
    val usergroup: String,
    val username: String,
    val min_pass: Int,
    val require_biometrics: Boolean,
    val require_encryption: Boolean,
    val company_id: String,
    val pin_type: String,
    val pin_max_tries: Int,
    val pin_lockout_time: Int,
    val is_locked : Boolean
)