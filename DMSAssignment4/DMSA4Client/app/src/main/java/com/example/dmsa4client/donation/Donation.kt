package com.example.dmsa4client.donation

import java.io.Serializable

// Donation object to display on UI
data class Donation(
    val id: String,
    val username: String,
    val food: String,
    val latitude: String,
    val longitude: String
) : Serializable