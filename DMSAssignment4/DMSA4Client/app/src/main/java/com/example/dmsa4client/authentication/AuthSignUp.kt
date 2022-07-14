package com.example.dmsa4client.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.dmsa4client.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

const val mes8 = "com.example.dmsa4client.authentication.mes8"

class AuthSignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_signup)
    }

    /** Called when the user touches the Submit button */
    fun submitForm(view: View) {
        var urlBuilder = "http://10.0.2.2:8080/DMSA4RestfulService/api/accounts/"
        val editUsername = findViewById<View>(R.id.signUpUsernameET) as EditText
        val editEmail = findViewById<View>(R.id.signUpEmailET) as EditText
        val editPassword = findViewById<View>(R.id.signUpPasswordET) as EditText
        val username = editUsername.text.toString()
        val email = editEmail.text.toString()
        val password = editPassword.text.toString()
        if (username.isNotEmpty() && username.isNotBlank() && email.isNotEmpty() && email.isNotBlank() && password.isNotEmpty() && password.isNotBlank()) {
            urlBuilder = "$urlBuilder$username/$email/$password"
            MainScope().launch(Dispatchers.IO) {
                makeAccount(urlBuilder)
            }
        }
    }

    /** Make POST http request to insert data into server's database */
    private fun makeAccount(urlBuilder: String) {
        val url = URL(urlBuilder)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8")
        conn.connect()
        var res = ""
        if (conn.responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    res = line.toString()
                }
            }
            Log.d("CHECK", res)
            conn.disconnect()
        }
        // display confirmation page with the info submitted
        val intent = Intent(this, AuthSignUpConfirmation::class.java).apply {
            putExtra(mes8, res)
        }
        startActivity(intent)
    }
}