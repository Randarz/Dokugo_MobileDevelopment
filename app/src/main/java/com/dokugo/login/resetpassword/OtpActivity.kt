package com.dokugo.login.resetpassword

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dokugo.R
import com.google.android.material.button.MaterialButton

class OtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val otpFields = listOf(
            findViewById<EditText>(R.id.otp_digit1),
            findViewById<EditText>(R.id.otp_digit2),
            findViewById<EditText>(R.id.otp_digit3),
            findViewById<EditText>(R.id.otp_digit4),
            findViewById<EditText>(R.id.otp_digit5),
            findViewById<EditText>(R.id.otp_digit6)
        )

        // Add TextWatcher to handle auto-focus
        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && index < otpFields.size - 1) {
                        otpFields[index + 1].requestFocus()
                    } else if (s?.isEmpty() == true && index > 0) {
                        otpFields[index - 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        val btConfOtp: MaterialButton = findViewById(R.id.bt_confotp)
        btConfOtp.setOnClickListener {
            val otp = otpFields.joinToString("") { it.text.toString() }
            if (otp.length == 6) {
                // Keep the original functionality
                val intent = Intent(this, ResetPassActivity::class.java)
                startActivity(intent)
            } else {
                // Show an error message
                Toast.makeText(this, "Please enter all 6 digits.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
