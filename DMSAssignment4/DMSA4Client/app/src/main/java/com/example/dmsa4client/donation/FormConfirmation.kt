package com.example.dmsa4client.donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.dmsa4client.R
import com.example.dmsa4client.authentication.mes9

class FormConfirmation : AppCompatActivity() {
    var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_confirmation)
        // Get the Intent that started this activity and extract the string
        username = intent.getStringExtra(mes9).toString()
        val food = intent.getStringExtra(mes2)

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.usernameConfirmed).apply {
            text = username
        }

        // Capture the layout's TextView and set the string as its text
        val textView2 = findViewById<TextView>(R.id.foodConfirmed).apply {
            text = food
        }
    }

    /** Called when the user touches the Home button */
    fun goHome(view: View) {
        val intent = Intent(this, DonationMainActivity::class.java).apply {
            putExtra(mes9, username)
        }
        startActivity(intent)
    }
}