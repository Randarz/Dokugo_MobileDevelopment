package com.dokugo.login.signin

import android.content.Intent
import android.os.Bundle
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

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()

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
    }
}