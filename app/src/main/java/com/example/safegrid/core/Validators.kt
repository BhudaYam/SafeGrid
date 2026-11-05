package com.example.safegrid.core

object Validators {
    private val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun email(value: String): Boolean = emailPattern.matches(value.trim())

    fun password(value: String): Boolean = value.length >= 6

    fun name(value: String): Boolean = value.trim().length >= 2

    fun description(value: String): Boolean = value.trim().length >= 8
}
