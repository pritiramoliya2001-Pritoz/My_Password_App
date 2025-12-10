package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import com.example.myapplication.data.entities.dataBase.AppDatabase
import com.example.myapplication.ui.theme.home.PasswordManagerApp
import com.example.myapplication.ui.theme.password.PasswordViewModel
import java.util.concurrent.Executor

class MainActivity : FragmentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var viewModel: PasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "password-db"
        ).build()

        viewModel = PasswordViewModel(database.passwordDao(), CryptoManager())

        showBiometricPrompt()
    }

    private fun showBiometricPrompt() {
        val executor: Executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    setContent {
                        PasswordManagerApp(viewModel)
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Auth failed", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        finish()
                    }
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
