package com.example.hotpet.api.models

import com.example.hotpet.utils.Category
import com.example.hotpet.utils.Gender
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

data class User(
    val _id: String?,
    val email: String,
    val password: String?,
    val username: String,
    val birthdate: Date,
    val gender: Gender,
    val category: Category,
    val about: String,

    // PREFERENCES
    var preferredAgeMin: Int,
    var preferredAgeMax: Int,
    var preferredDistance: Int,

    // LOCATION
    val latitude: Double?,
    val longitude: Double?,

    // OTHERS
    val imageFilename: String,
    val images: ArrayList<String>,
    val isVerified: Boolean?,
) : Serializable