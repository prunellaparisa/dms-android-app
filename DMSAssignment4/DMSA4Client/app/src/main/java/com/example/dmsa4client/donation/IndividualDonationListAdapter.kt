package com.example.dmsa4client.donation

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dmsa4client.R
import com.example.dmsa4client.authentication.mes9
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class IndividualDonationListAdapter(private val donations: ArrayList<Donation>) :
    RecyclerView.Adapter<IndividualDonationListAdapter.IndividualDonationViewHolder>() {
    class IndividualDonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IndividualDonationViewHolder {
        return IndividualDonationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.individual_item_donation,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: IndividualDonationViewHolder, position: Int) {
        val curDonation = donations[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.individualTVUsername).text = curDonation.username
            findViewById<TextView>(R.id.individualTVFood).text = curDonation.food
            findViewById<Button>(R.id.individualDeleteBTN).setOnClickListener {
                // button leads to a map using its specific location
                // delete todo
                var id = curDonation.id
                var urlBuilder = "http://10.0.2.2:8080/DMSA4RestfulService/api/donations/$id"
                MainScope().launch(Dispatchers.IO) {
                    deleteDonation(urlBuilder)
                }
                val intent = Intent(context, DonationList::class.java).apply {
                    putExtra(mes9, curDonation.username)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return donations.size
    }

    /** Make DELETE http request to insert data into server's database */
    private fun deleteDonation(urlBuilder: String) {
        val url = URL(urlBuilder)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "DELETE"
        conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8")
        conn.connect()
        if (conn.responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            conn.disconnect()
        }
    }
}