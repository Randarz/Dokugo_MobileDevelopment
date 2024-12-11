package com.dokugo.login.resetpassword

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dokugo.R
import com.dokugo.data.network.RetrofitInstance
import com.dokugo.data.repository.UserRepository
import com.dokugo.login.signin.SignInActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ResetPassActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var resetToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)

        userRepository = UserRepository(RetrofitInstance.api)
        resetToken = intent.getStringExtra("resetToken") ?: ""

        val btResetPass: MaterialButton = findViewById(R.id.bt_resetpass)
        btResetPass.setOnClickListener {
            val passwordEditText: TextInputEditText = findViewById(R.id.passwordEditText)
            val newPassword = passwordEditText.text.toString().trim()
            if (newPassword.isNotEmpty()) {
                resetPassword(resetToken, newPassword)
            } else {
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(resetToken: String, newPassword: String) {
        lifecycleScope.launch {
            try {
                val response = userRepository.resetPassword(resetToken, newPassword)
                if (!response.error) {
                    Toast.makeText(this@ResetPassActivity, "Password reset successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ResetPassActivity, SignInActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ResetPassActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ResetPassActivity, "Failed to reset password: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}