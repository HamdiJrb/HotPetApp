package com.example.hotpet.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.R
import com.example.hotpet.api.UserService
import com.example.hotpet.api.viewModels.UserViewModel
import com.example.hotpet.utils.AlertMaker
import com.example.hotpet.utils.Category
import com.example.hotpet.utils.Gender
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


class CompleteRegistrationActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var userViewModel: UserViewModel

    // VIEWS
    private var aboutTIET: TextInputEditText? = null
    private var birthdateDP: DatePicker? = null
    private var genderRG: RadioGroup? = null
    private var categoryRG: RadioGroup? = null
    private var registerButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_registration)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // VIEW BINDING
        aboutTIET = findViewById(R.id.aboutTIET)
        birthdateDP = findViewById(R.id.birthdateDP)
        genderRG = findViewById(R.id.genderRG)
        categoryRG = findViewById(R.id.categoryRG)
        registerButton = findViewById(R.id.registerButton)

        registerButton!!.setOnClickListener { register() }

        setupObservers()
    }

    private fun setupObservers() {
        userViewModel.user.observe(this) {
            AlertMaker.makeActionAlert(
                this,
                "Success",
                "Registration successful, please verify your email to login"
            ) {
                startActivity(Intent(this@CompleteRegistrationActivity, LoginActivity::class.java))
                finish()
            }
        }
        userViewModel.errorMessage.observe(this) {
            AlertMaker.makeAlert(this, "Error", it)
        }
    }

    var category: Category? = null
    var gender: Gender? = null

    private fun register() {

        val birthdate = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        ).parse(
            birthdateDP!!.dayOfMonth.toString() + "-" +
                    birthdateDP!!.month.toString() + "-" +
                    birthdateDP!!.year.toString()
        )

        gender = when (genderRG!!.checkedRadioButtonId) {
            R.id.maleRB -> {
                Gender.Male
            }
            R.id.femaleRB -> {
                Gender.Female
            }
            else -> {
                null
            }
        }

        category = when (categoryRG!!.checkedRadioButtonId) {
            R.id.dogRB -> {
                Category.Dog
            }
            R.id.catRB -> {
                Category.Cat
            }
            R.id.horseRB -> {
                Category.Horse
            }
            else -> {
                null
            }
        }

        if (inputControl()) {
            userViewModel.register(
                UserService.RegisterBody(
                    intent.getStringExtra("EMAIL")!!,
                    intent.getStringExtra("PASSWORD")!!,
                    intent.getStringExtra("USERNAME")!!,
                    birthdate!!,
                    gender!!,
                    category!!,
                    aboutTIET!!.text.toString(),
                )
            )
        }
    }

    private fun inputControl(): Boolean {
        if (gender == null) {
            AlertMaker.makeAlert(this, "Warning", "Please choose a gender")
            return false
        }

        if (category == null) {
            AlertMaker.makeAlert(this, "Warning", "Please choose a category")
            return false
        }

        if (birthdateDP == null) {
            AlertMaker.makeAlert(this, "Warning", "Please choose a date")
            return false
        }

        return true
    }
}