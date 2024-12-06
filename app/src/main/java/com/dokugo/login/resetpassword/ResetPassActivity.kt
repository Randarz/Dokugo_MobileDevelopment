package com.dokugo.login.resetpassword

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dokugo.R
import com.dokugo.login.signin.SignInActivity
import com.google.android.material.button.MaterialButton

class ResetPassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)

        val btConfOtp: MaterialButton = findViewById(R.id.bt_resetpass)
        btConfOtp.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}