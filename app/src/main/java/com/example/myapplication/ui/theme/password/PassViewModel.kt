package com.example.myapplication.ui.theme.password

import PasswordEntity
import com.example.myapplication.CryptoManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.dao.PasswordDao
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class PasswordViewModel(
    private val dao: PasswordDao,
    private val cryptoManager: CryptoManager
) : ViewModel() {

    val passwordList = dao.getAllPasswords()

    fun addPassword(account: String, user: String, pass: String) {
        viewModelScope.launch {
            val passAsBytes = pass.encodeToByteArray()
            val outputStream = ByteArrayOutputStream()
            cryptoManager.encrypt(passAsBytes, outputStream)
            val encryptedBytes = outputStream.toByteArray()
            dao.insertPassword(PasswordEntity(accountType = account, username = user, encryptedPassword = encryptedBytes))
        }
    }

    fun deletePassword(entity: PasswordEntity) {
        viewModelScope.launch {
            dao.deletePassword(entity)
        }
    }

    fun decryptPassword(encrypted: ByteArray): String {
        val inputStream = ByteArrayInputStream(encrypted)
        val decryptedBytes = cryptoManager.decrypt(inputStream)
        return decryptedBytes.decodeToString()
    }

    // Bonus: Password Generator
    fun generatePassword(): String {
        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%"
        return (1..12).map { chars.random() }.joinToString("")
    }

    // Bonus: Strength Meter Logic
    fun calculateStrength(pass: String): Float {
        var score = 0f
        if (pass.length > 8) score += 0.2f
        if (pass.any { it.isDigit() }) score += 0.2f
        if (pass.any { it.isUpperCase() }) score += 0.2f
        if (pass.any { "!@#$%^&*".contains(it) }) score += 0.4f
        return score.coerceIn(0f, 1f)
    }
}
