package com.example.domain.model

data class LiveChannel(
    val id: String,
    val name: String,
    val category: String,
    val logoUrl: String,
    val streamUrl: String,
    val userAgent: String? = null,
    val cookie: String? = null,
    val licenseKey: String? = null,
    val isHd: Boolean = false
)
