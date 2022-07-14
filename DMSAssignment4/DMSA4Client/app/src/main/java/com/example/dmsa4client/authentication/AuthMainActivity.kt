package com.example.dmsa4client.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dmsa4client.R

class AuthMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_main)
    }

    /** Called when the user touches the Sign Up button */
    fun signUp(view: View) {
        val intent = Intent(this, AuthSignUp::class.java)
        startActivity(intent)
    }

    /** Called when the user touches the Log In button */
    fun logIn(view: View) {
        val intent = Intent(this, AuthLogIn::class.java)
        startActivity(intent)
    }
}