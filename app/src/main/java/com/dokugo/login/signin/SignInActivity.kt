package com.dokugo.login.signin

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dokugo.MainActivity
import com.dokugo.R
import com.dokugo.login.resetpassword.ForgetPasswordActivity
import com.dokugo.login.signup.SignUpActivity
import com.dokugo.ui.home.HomeFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.dokugo.databinding.ActivitySignInBinding
import com.dokugo.ui.ComingsoonFragment


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        PlayAnimation()


        val tvSignUp: TextView = findViewById(R.id.tv_signup)
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val BtSignIn: MaterialButton = findViewById(R.id.bt_signin)
        BtSignIn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

    private fun openComingSoonFragment() {
        // Mengganti fragment yang ada dengan ComingsoonFragment secara penuh
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, ComingsoonFragment()) // Menggunakan android.R.id.content untuk menggantikan seluruh layar
            .addToBackStack(null) // Menambahkan fragment ke back stack agar bisa kembali ke SignInActivity
            .commit()

        // Setelah 2 detik, tutup fragment dan kembali ke SignInActivity
        Handler(Looper.getMainLooper()).postDelayed({
            supportFragmentManager.popBackStack() // Tutup fragment
        }, 2000)
    }


    private fun PlayAnimation() {
        with(binding) {

            animateView(icon, "alpha", 0f, 1f, 0)
            animateView(tvWelcome, "translationY", 500f, 0f, 200)
            animateView(tvSignin, "translationY", 500f, 0f, 400)

            // Paket 2: username dan layout username dijalankan bersama dengan tvPassword dan passwordComponent
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

