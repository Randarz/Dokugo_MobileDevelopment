package com.dokugo.login.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dokugo.MainActivity
import com.dokugo.R
import com.dokugo.databinding.ActivitySignUpBinding
import com.dokugo.data.repository.UserRepository
import com.dokugo.data.network.RetrofitInstance
import com.dokugo.login.signin.SignInActivity
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        userRepository = UserRepository(RetrofitInstance.api)

        PlayAnimation(binding.tvSignin)
        PlayAnimation(binding.usernameComponent)
        PlayAnimation(binding.emailComponent)
        PlayAnimation(binding.passwordComponent)
        PlayAnimation(binding.phoneNumberComponent)

        binding.tvSignin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            val username = binding.usernameComponent.editText?.text.toString().trim()
            val email = binding.emailComponent.editText?.text.toString().trim()
            val password = binding.passwordComponent.editText?.text.toString().trim()
            val phoneNumber = binding.phoneNumberComponent.editText?.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && phoneNumber.isNotEmpty()) {
                registerUser(username, email, password, phoneNumber)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(username: String, email: String, password: String, phoneNumber: String) {
        lifecycleScope.launch {
            try {
                val response = userRepository.registerUser(username, email, password, phoneNumber)
                if (!response.error) {
                    Toast.makeText(this@SignUpActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@SignUpActivity, response.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignUpActivity, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun PlayAnimation(view: View) {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        view.startAnimation(fadeIn)
    }
}