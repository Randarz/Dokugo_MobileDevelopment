package com.dokugo.login.signin

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dokugo.MainActivity
import com.dokugo.R
import com.dokugo.databinding.ActivitySignInBinding
import com.dokugo.login.resetpassword.ForgetPasswordActivity
import com.dokugo.login.signup.SignUpActivity
import com.dokugo.ui.ComingsoonFragment
import com.dokugo.data.repository.UserRepository
import com.dokugo.data.network.RetrofitInstance
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        userRepository = UserRepository(RetrofitInstance.api)

        PlayAnimation()

        val tvSignUp: TextView = findViewById(R.id.tv_signup)
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val BtSignIn: MaterialButton = findViewById(R.id.bt_signin)
        BtSignIn.setOnClickListener {
            val email = binding.usernameComponent.editText?.text.toString().trim()
            val password = binding.passwordComponent.editText?.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        val tvForgot: TextView = findViewById(R.id.forgetPass)
        tvForgot.setOnClickListener {
            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.google.setOnClickListener {
            openComingSoonFragment()
        }
        binding.facebook.setOnClickListener {
            openComingSoonFragment()
        }
        binding.apple.setOnClickListener {
            openComingSoonFragment()
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = userRepository.loginUser(email, password)
                if (!response.error) {
                    // Save token and navigate to the main activity
                    val token = response.token
                    onLoginSuccess(token)
                    Toast.makeText(this@SignInActivity, "Login successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SignInActivity, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignInActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onLoginSuccess(token: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()

        // Navigate to the next screen
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openComingSoonFragment() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, ComingsoonFragment())
            .addToBackStack(null)
            .commit()

        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.popBackStack()
        }, 2000)
    }

    private fun PlayAnimation() {
        with(binding) {
            animateView(icon, "alpha", 0f, 1f, 0)
            animateView(tvWelcome, "translationY", 500f, 0f, 200)
            animateView(tvSignin, "translationY", 500f, 0f, 400)
            animateView(tvUsername, "translationY", 500f, 0f, 600)
            animateView(usernameComponent, "translationY", 500f, 0f, 600)
            animateView(tvPassword, "translationY", 500f, 0f, 600)
            animateView(passwordComponent, "translationY", 500f, 0f, 600)
            animateView(forgetPass, "translationY", 500f, 0f, 800)
            animateView(btSignin, "alpha", 0f, 1f, 1000)
            animateView(lineWithText, "alpha", 0f, 1f, 1200)
            animateView(apple, "alpha", 0f, 1f, 1400)
            animateView(google, "alpha", 0f, 1f, 1600)
            animateView(facebook, "alpha", 0f, 1f, 1800)
            animateView(noacc, "alpha", 0f, 1f, 2000)
            animateView(tvSignup, "alpha", 0f, 1f, 2200)
        }
    }

    private fun animateView(view: View, property: String, startValue: Float, endValue: Float, delay: Long) {
        view.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(view, property, startValue, endValue).apply {
            duration = 1000
            startDelay = delay
            start()
        }
    }
}