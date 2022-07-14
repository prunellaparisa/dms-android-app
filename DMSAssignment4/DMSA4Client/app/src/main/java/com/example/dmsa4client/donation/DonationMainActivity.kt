package com.example.dmsa4client.donation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dmsa4client.R
import com.example.dmsa4client.authentication.AuthLogIn
import com.example.dmsa4client.authentication.mes9
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import java.io.Serializable
import java.net.URL
import java.nio.charset.Charset

const val mes3 = "com.example.dmsa4client.donation.mes3"

class DonationMainActivity : AppCompatActivity() {
    var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donation_main)

        username = intent.getStringExtra(mes9).toString()

        // Capture the layout's TextView and set the string as its text
        findViewById<TextView>(R.id.textView).apply {
            text = "Welcome $username!"
        }
    }

    /** Called when the user touches the Donate Food button */
    fun openDonationForm(view: View) {
        val intent = Intent(this, DonationForm::class.java).apply {
            putExtra(mes9, username)
        }
        startActivity(intent)
    }

    /** Called when the user touches the Log Out button */
    fun logOut(view: View) {
        val intent = Intent(this, AuthLogIn::class.java)
        startActivity(intent)
    }

    /** Called when the user touches the See Donations button */
    fun seeDonations(view: View) {
        // make http request
        var urlBuilder = "http://10.0.2.2:8080/DMSA4RestfulService/api/donations/"
        MainScope().launch(Dispatchers.IO) {
            getDonations(urlBuilder)
        }
    }

    /** Called when the user touches the See Donations button */
    fun seeYourDonations(view: View) {
        // make http request
        var urlBuilder = "http://10.0.2.2:8080/DMSA4RestfulService/api/donations/$username"
        MainScope().launch(Dispatchers.IO) {
            getOwnDonations(urlBuilder)
        }
    }

    /** make GET http request and prepare info to display in next page */
    private fun getDonations(urlBuilder: String) {
        val url = URL(urlBuilder).readText(Charset.defaultCharset())
        val test = parse(url)

        if (test != null) {
            val donations: ArrayList<Donation> = ArrayList()
            for (i in 0 until test.length()) {
                // username
                val id = test.getJSONObject(i).getString("id")
                // username
                val username = test.getJSONObject(i).getString("username")
                // food
                val food = test.getJSONObject(i).getString("food")
                // latitude
                val latitude = test.getJSONObject(i).getString("latitude")
                // longitude
                val longitude = test.getJSONObject(i).getString("longitude")
                donations.add(Donation(id, username, food, latitude, longitude))
            }

            val bundle = Bundle()
            bundle.putSerializable("donations", donations as Serializable?)
            val intent = Intent(this, DonationList::class.java).apply {
                putExtra(mes3, bundle)
                putExtra(mes9, username)
            }
            startActivity(intent)
        }
    }

    /** make GET http request and prepare info to display in next page */
    private fun getOwnDonations(urlBuilder: String) {
        val url = URL(urlBuilder).readText(Charset.defaultCharset())
        val test = parse(url)

        if (test != null) {
            val donations: ArrayList<Donation> = ArrayList()
            for (i in 0 until test.length()) {
                // username
                val id = test.getJSONObject(i).getString("id")
                // username
                val username = test.getJSONObject(i).getString("username")
                // food
                val food = test.getJSONObject(i).getString("food")
                // latitude
                val latitude = test.getJSONObject(i).getString("latitude")
                // longitude
                val longitude = test.getJSONObject(i).getString("longitude")
                donations.add(Donation(id, username, food, latitude, longitude))
            }

            val bundle = Bundle()
            bundle.putSerializable("donations", donations as Serializable?)
            val intent = Intent(this, IndividualDonationList::class.java).apply {
                putExtra(mes3, bundle)
                putExtra(mes9, username)
            }
            startActivity(intent)
        }
    }

    // parse http response and make the json readable
    private fun parse(json: String): JSONArray? {
        var jsonObject: JSONArray? = null
        try {
            jsonObject = JSONArray(json)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}