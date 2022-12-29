package com.example.secureFolderManagement.models

import com.example.secureFolderManagement.entities.LogEntity

data class LogRequest(
    val log : List<LogEntity>
)
