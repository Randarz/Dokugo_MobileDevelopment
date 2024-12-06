package com.dokugo.login.resetpassword

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dokugo.R
import com.dokugo.login.signin.SignInActivity
import com.google.android.material.button.MaterialButton

class ForgetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

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

        val btSendEmail : MaterialButton = findViewById(R.id.bt_sendemail)
        btSendEmail.setOnClickListener{
            val intent = Intent(this, OtpActivity::class.java)
            startActivity(intent)
        }
    }
}