package com.example.dmsa4client.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.dmsa4client.R
import com.example.dmsa4client.donation.DonationMainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

const val mes9 = "com.example.dmsa4client.authentication.mes9"

class AuthLogIn : AppCompatActivity() {
    var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_log_in)
    }

    /** Called when the user touches the Submit button */
    fun submitForm(view: View) {
        var urlBuilder = "http://10.0.2.2:8080/DMSA4RestfulService/api/accounts/"
        val editUsername = findViewById<View>(R.id.logInUsernameET) as EditText
        val editPassword = findViewById<View>(R.id.logInPasswordET) as EditText
        username = editUsername.text.toString()
        val password = editPassword.text.toString()
        if (username.isNotEmpty() && username.isNotBlank() && password.isNotEmpty() && password.isNotBlank()) {
            urlBuilder = "$urlBuilder$username/$password"
            MainScope().launch(Dispatchers.IO) {
                verifyAccount(urlBuilder)
            }
        }
    }

    /** Make POST http request to verify data in server's database */
    private fun verifyAccount(urlBuilder: String) {
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

        if (res == "valid account") {
            // display confirmation page with the info submitted
            val intent = Intent(this, DonationMainActivity::class.java).apply {
                putExtra(mes9, username)
            }
            startActivity(intent)
        } else {
            // Capture the layout's TextView and set the string as its text
            this.runOnUiThread {
                findViewById<TextView>(R.id.logInErrorTV).apply {
                    text = "Error: $res"
                }
            }
        }
    }
}