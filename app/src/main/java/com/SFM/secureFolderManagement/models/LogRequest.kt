package com.SFM.secureFolderManagement.models

import com.SFM.secureFolderManagement.entities.LogEntity

data class LogRequest(
    val log : List<LogEntity>
)
