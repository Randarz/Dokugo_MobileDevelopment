package com.dokugo.login.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dokugo.R
import com.dokugo.databinding.ActivitySignUpBinding
import com.dokugo.login.signin.SignInActivity
import com.google.android.material.button.MaterialButton

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PlayAnimation(binding.btSignin)
        PlayAnimation(binding.tvSignin)
        PlayAnimation(binding.usernameComponent)
        PlayAnimation(binding.emailComponent)
        PlayAnimation(binding.passwordComponent)

        binding.btSignin.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.tvSignin.setOnClickListener {
            // Create an Intent to open the SignInActivity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun PlayAnimation(view: View) {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        view.startAnimation(fadeIn)
    }

}