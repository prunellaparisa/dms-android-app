package com.example.dmsa4client.donation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dmsa4client.R
import com.example.dmsa4client.authentication.mes9
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

const val mes = "com.example.dmsa4client.donation.mes"
const val mes2 = "com.example.dmsa4client.donation.mes2"

class DonationForm : AppCompatActivity() {
    private var username = ""
    private var food = ""
    private var latitude = ""
    private var longitude = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_form)
        // Get the Intent that started this activity and extract the string
        username = intent.getStringExtra(mes9).toString()
        food = intent.getStringExtra(mes2).toString()
        latitude = intent.getStringExtra(mes4).toString()
        longitude = intent.getStringExtra(mes5).toString()

        // Capture the layout's TextView and set the string as its text
        val textView = findViewById<TextView>(R.id.latitudeConfirmed).apply {
            text = if (latitude == "null" && longitude == "null") {
                "Please select a location."
            } else {
                ""
            }
        }

        // Capture the layout's TextView and set the string as its text
        val textView4 = findViewById<EditText>(R.id.editFood).apply {
            if (food == "null") {
                setText("")
            } else {
                setText(food)
            }
        }

    }

    /** Called when the user touches the Back button */
    fun goHome(view: View) {
        val intent = Intent(this, DonationMainActivity::class.java).apply {
            putExtra(mes9, username)
        }
        startActivity(intent)
    }

    /** Called when the user touches the View Location button */
    fun viewLocation(view: View) {
        val editFood = findViewById<View>(R.id.editFood) as EditText
        food = editFood.text.toString()
        val intent = Intent(this, MapsActivity::class.java).apply {
            putExtra(mes9, username)
            putExtra(mes2, food)
        }
        startActivity(intent)
    }

    /** Called when the user touches the Submit button */
    fun submitForm(view: View) {
        var urlBuilder = "http://10.0.2.2:8080/DMSA4RestfulService/api/donations/"
        val editFood = findViewById<View>(R.id.editFood) as EditText
        food = editFood.text.toString()
        if (username.isNotEmpty() && username.isNotBlank() && food.isNotEmpty() && food.isNotBlank() && !latitude.isNullOrEmpty() && !longitude.isNullOrEmpty()) {
            urlBuilder = "$urlBuilder$username/$food/$latitude/$longitude"
            MainScope().launch(Dispatchers.IO) {
                putDonation(urlBuilder)
            }
            // display confirmation page with the info submitted
            val intent = Intent(this, FormConfirmation::class.java).apply {
                putExtra(mes9, username)
                putExtra(mes2, food)
            }
            startActivity(intent)
        }
    }

    /** Make PUT http request to insert data into server's database */
    private fun putDonation(urlBuilder: String) {
        val url = URL(urlBuilder)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "PUT"
        conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8")
        conn.connect()
        if (conn.responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            conn.disconnect()
        }
    }
}