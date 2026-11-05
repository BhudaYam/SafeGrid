package com.example.safegrid.core

import java.security.MessageDigest

/**
 * Turns the password the user typed into a SHA-256 hash.
 * Firebase Authentication receives this hash, never the original password.
 */
object PasswordHasher {
    fun sha256(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }
}
