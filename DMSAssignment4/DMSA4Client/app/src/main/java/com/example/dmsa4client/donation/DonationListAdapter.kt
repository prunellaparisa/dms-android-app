package com.example.dmsa4client.donation

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dmsa4client.R

const val mes6 = "com.example.dmsa4client.donation.mes6"
const val mes7 = "com.example.dmsa4client.donation.mes7"

// translate list of Donation objects into UI
class DonationListAdapter(private val donations: ArrayList<Donation>) :
    RecyclerView.Adapter<DonationListAdapter.DonationViewHolder>() {

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        return DonationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_donation,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val curDonation = donations[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.textviewUsername).text = curDonation.username
            findViewById<TextView>(R.id.textviewFood).text = curDonation.food
            findViewById<Button>(R.id.seeLocationButton).setOnClickListener {
                // button leads to a map using its specific location
                val intent = Intent(context, IndividualMapsActivity::class.java).apply {
                    putExtra(mes6, curDonation.latitude)
                    putExtra(mes7, curDonation.longitude)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return donations.size
    }

}
