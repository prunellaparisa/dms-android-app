package com.example.dmsa4client.donation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dmsa4client.R
import com.example.dmsa4client.authentication.mes9

class IndividualDonationList : AppCompatActivity() {
    private var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_donation_list)
        username = intent.getStringExtra(mes9).toString()
        // Get the Intent that started this activity and extract the string
        val donations = intent.getBundleExtra(mes3)?.getSerializable("donations")
        var rv = findViewById<RecyclerView>(R.id.idlDonationList)
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = IndividualDonationListAdapter(donations as ArrayList<Donation>)
        rv.adapter = adapter
    }

    /** Called when the user touches the Home button */
    fun goHome(view: View) {
        val intent = Intent(this, DonationMainActivity::class.java).apply {
            putExtra(mes9, username)
        }
        startActivity(intent)
    }
}