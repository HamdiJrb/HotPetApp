package tn.esprit.smartpet.model

class LoginResponse(
    val isError: Boolean,
    val message: String,
    val user: User
    )