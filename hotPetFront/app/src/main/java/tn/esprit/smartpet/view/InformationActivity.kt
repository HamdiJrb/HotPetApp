package tn.esprit.smartpet.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.adapters.CalendarViewBindingAdapter.setDate
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartpet.R
import tn.esprit.smartpet.api.ApiInterface
import tn.esprit.smartpet.model.User
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

const val IMAGE = "IMAGE"
const val GENDER = "GENDER"
const val CATEGORIE = "CATEGORIE"
const val AGE = "AGE"

class InformationActivity : AppCompatActivity() {

    var call = ApiInterface.create()  // call

    private lateinit var btn_skip: Button
    private lateinit var profilePic: ImageView
    private lateinit var rbMale: RadioButton
    private lateinit var rbFemale: RadioButton
    val categories = arrayOf("Cat", "Dog", "Horse", "Rabit", "Bird", "Hamster")
    private lateinit var datePicker: DatePicker
    private var msg: String? = null
    private lateinit var btn_confirm: Button

    // Pour la recuperation des input
    private lateinit var animalType: String
    private lateinit var ProfilePicture: String


    private lateinit var selectedImageUri: Uri
    private val startForResultOpenGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data!!.data!!
                profilePic!!.setImageURI(selectedImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        // Spinner " I am "
        val spinner = findViewById<Spinner>(R.id.spinner)
        val arrayAdapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,categories)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) { animalType = categories[position]    // ANIMAL TYPE
                Toast.makeText(applicationContext,"You're a "+animalType,Toast.LENGTH_SHORT).show() }
            override fun onNothingSelected(parent: AdapterView<*>?) { TODO("Not yet implemented") }
        }


        btn_skip = findViewById(R.id.btn_skip)
        profilePic = findViewById(R.id.addImage)
        rbMale = findViewById(R.id.rbMale)
        rbFemale = findViewById(R.id.rbFemale)
        datePicker = findViewById<DatePicker>(R.id.datePicker)
        btn_confirm = findViewById(R.id.btn_confirm)

        btn_skip.setOnClickListener {
            val main_intent = Intent(this, Home::class.java)
            startActivity(main_intent)
        }

        profilePic!!.setOnClickListener {
            openGallery()
        }

        // Birthday date
        val today = Calendar.getInstance()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)

        ) { view, year, month, day ->
            val month = month + 1
            msg = " $day/$month/$year"
            Toast.makeText(this@InformationActivity, msg, Toast.LENGTH_SHORT).show()
        }

        btn_confirm.setOnClickListener {
            clickConfirm()
        }
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
    fun getAge(year: Int, month: Int, dayOfMonth: Int): Int {
        return Period.between(LocalDate.of(year, month, dayOfMonth), LocalDate.now()).years
    }*/

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startForResultOpenGallery.launch(intent)
    }



    private fun clickConfirm() {
        if (validate()) {
            /*val user = User(ProfilePicture, animalType)
            call.signup(user).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@InformationActivity,
                            "User added successfully",
                            Toast.LENGTH_SHORT
                        ).show()*/
            val intent =
                Intent(this@InformationActivity, Home::class.java)
            startActivity(intent)
            /*} else {
                        Toast.makeText(
                            this@InformationActivity,
                            "User already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@InformationActivity, "Ooooops !! ", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }*/
        }
    }

    private fun validate(): Boolean {

        if (selectedImageUri == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Snackbar.make(
                    findViewById(R.id.constraint_layout),
                    "Please select a picture !",
                    Snackbar.LENGTH_SHORT
                ).setBackgroundTint(getColor(R.color.primaryLightColor)).show()
            }
            return false
        }
        return true
    }



    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}