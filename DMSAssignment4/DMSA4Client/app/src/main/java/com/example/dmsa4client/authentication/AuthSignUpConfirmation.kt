package com.example.dmsa4client.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.dmsa4client.R

class AuthSignUpConfirmation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_sign_up_confirmation)
        val state = intent.getStringExtra(mes8)

        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.confirmationStateTV).apply {
            text = state
        }
    }

    /** Called when the user touches the Log In button */
    fun logIn(view: View) {
        val intent = Intent(this, AuthLogIn::class.java)
        startActivity(intent)
    }
}