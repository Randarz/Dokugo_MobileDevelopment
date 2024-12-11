package com.dokugo.login.resetpassword

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        userRepository = UserRepository(RetrofitInstance.api)

        val icBack: ImageView = findViewById(R.id.ic_back)
        icBack.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val tvLogin: TextView = findViewById(R.id.tv_textlogin)
        tvLogin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        val btSendEmail: MaterialButton = findViewById(R.id.bt_sendemail)
        btSendEmail.setOnClickListener {
            val emailEditText: TextInputEditText = findViewById(R.id.emailEditText)
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                sendOtp(email)
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOtp(email: String) {
        lifecycleScope.launch {
            try {
                val response = userRepository.sendOtp(email)
                if (!response.error) {
                    Toast.makeText(this@ForgetPasswordActivity, "OTP sent to your email", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ForgetPasswordActivity, OtpActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ForgetPasswordActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ForgetPasswordActivity, "Failed to send OTP: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}