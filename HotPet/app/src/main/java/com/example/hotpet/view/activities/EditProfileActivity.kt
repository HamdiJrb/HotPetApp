package com.example.hotpet.view.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.hotpet.R
import com.example.hotpet.api.UserService
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.UserViewModel
import com.example.hotpet.utils.*
import com.example.hotpet.utils.UserSession.getSession
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var userViewModel: UserViewModel
    private var userSession: User? = null

    // VIEWS
    private var emailTIET: TextInputEditText? = null
    private var usernameTIET: TextInputEditText? = null
    private var aboutTIET: TextInputEditText? = null
    private var birthdateDP: DatePicker? = null
    private var genderRG: RadioGroup? = null
    private var categoryRG: RadioGroup? = null
    private var maleRB: RadioButton? = null
    private var femaleRB: RadioButton? = null
    private var dogRB: RadioButton? = null
    private var catRB: RadioButton? = null
    private var horseRB: RadioButton? = null
    private var profilePictureIV: ImageView? = null
    private var editProfileButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // VIEW BINDING
        emailTIET = findViewById(R.id.emailTIET)
        usernameTIET = findViewById(R.id.usernameTIET)
        aboutTIET = findViewById(R.id.aboutTIET)
        birthdateDP = findViewById(R.id.birthdateDP)
        genderRG = findViewById(R.id.genderRG)
        categoryRG = findViewById(R.id.categoryRG)
        maleRB = findViewById(R.id.maleRB)
        femaleRB = findViewById(R.id.femaleRB)
        dogRB = findViewById(R.id.dogRB)
        catRB = findViewById(R.id.catRB)
        horseRB = findViewById(R.id.horseRB)
        editProfileButton = findViewById(R.id.editProfileButton)
        profilePictureIV = findViewById(R.id.profilePictureIV)

        // ACTIONS
        editProfileButton!!.setOnClickListener { updateProfile() }

        loadUser()
        initObservers()
    }

    private fun initObservers() {
        userViewModel.user.observe(this) {
            userSession = it
            initEditProfile()
        }
    }

    private fun loadUser() {
        userSession = getSession(this@EditProfileActivity)
        userViewModel.getById(userSession!!._id!!)
    }

    private fun initEditProfile() {
        val birthdate = GregorianCalendar()
        println(userSession)
        birthdate.time = userSession!!.birthdate

        emailTIET!!.setText(userSession!!.email)
        usernameTIET!!.setText(userSession!!.username)
        aboutTIET!!.setText(userSession!!.about)
        birthdateDP!!.init(
            birthdate[Calendar.YEAR],
            birthdate[Calendar.MONTH],
            birthdate[Calendar.DAY_OF_MONTH],
            null
        )

        if (userSession!!.gender == Gender.Male) {
            maleRB!!.isChecked = true
        } else {
            femaleRB!!.isChecked = true
        }

        when (userSession!!.category) {
            Category.Dog -> {
                dogRB!!.isChecked = true
            }
            Category.Cat -> {
                catRB!!.isChecked = true
            }
            else -> {
                horseRB!!.isChecked = true
            }
        }

        Glide.with(baseContext)
            .load(Constants.BASE_URL_IMAGES + userSession!!.imageFilename)
            .into(profilePictureIV!!)
    }

    var category: Category? = null
    var gender: Gender? = null

    private fun updateProfile() {
        val birthdate = SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        ).parse(
            birthdateDP!!.dayOfMonth.toString() + "-" +
                    birthdateDP!!.month.toString() + "-" +
                    birthdateDP!!.year.toString()
        )!!

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
            userViewModel.updateProfile(
                UserService.UpdateProfileBody(
                    emailTIET!!.text.toString(),
                    usernameTIET!!.text.toString(),
                    birthdate,
                    gender!!,
                    category!!,
                    aboutTIET!!.text.toString()
                )
            )

            userViewModel.user.observe(this) {
                UserSession.saveSession(this, it)
                finish()
            }
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