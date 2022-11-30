package tn.esprit.smartpet.model
import com.google.gson.annotations.SerializedName


// 1. Class 3andha nafs l attributs li fi json
data class User(
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    var password: String? = null,

    @SerializedName("username")
    var username: String? = null,

    @SerializedName("gender")
    var gender: String? = null,

    @SerializedName("age")
    var age: String? = null,

    @SerializedName("aboutme")
    var aboutme: String? = null,

    @SerializedName("typeanimal")
    var typeanimal: String? = null,

    @SerializedName("ProfilePicture")
    var ProfilePicture: String? = null,
    )