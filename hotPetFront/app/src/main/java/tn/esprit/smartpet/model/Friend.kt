package tn.esprit.smartpet.model

import androidx.annotation.DrawableRes

const val PICTURE = "PICTURE"
const val NAME = "NAME"
const val MESSAGE = "MESSAGE"

data class Friend(

    @DrawableRes
    val friendPic: Int,

    val friendName: String,

    val friendMessage: String
)