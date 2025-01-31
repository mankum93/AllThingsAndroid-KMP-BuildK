package com.allthingsandroid.kmp.plugin.util


fun validateRequiredFields(fields: Map<String, Any?>): Boolean {
    val missingFields = fields.filterValues { it == null || (it is CharSequence && it.isBlank()) }.keys
    if (missingFields.isNotEmpty()) {
        println("BuildKConfigPlugin: The plugin cannot be applied. Please define a valid for the following: '${missingFields.joinToString(", ")}'.")
        return false
    }
    return true
}