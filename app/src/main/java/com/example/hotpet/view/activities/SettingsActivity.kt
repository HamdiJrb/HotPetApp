package com.example.hotpet.view.activities

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.R
import com.example.hotpet.api.UserService
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.UserViewModel
import com.example.hotpet.utils.UserSession
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider

class SettingsActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var userViewModel: UserViewModel
    private var sessionUser: User? = null

    // VIEWS
    private var darkModeSwitch: SwitchCompat? = null
    private var ageRangeSlider: RangeSlider? = null
    private var distanceSlider: Slider? = null
    private var saveChangesButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        sessionUser = UserSession.getSession(this)

        // VIEW BINDING
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        ageRangeSlider = findViewById(R.id.ageRangeSlider)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        distanceSlider = findViewById(R.id.distanceSlider)

        val nightModeFlags: Int = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> darkModeSwitch!!.isChecked = true
            Configuration.UI_MODE_NIGHT_NO -> darkModeSwitch!!.isChecked = false
        }

        val minAge = 1
        val maxAge = 20
        val minDistance = 1
        val maxDistance = 150

        ageRangeSlider!!.stepSize = 1.0F
        distanceSlider!!.stepSize = 1.0F

        if ((sessionUser!!.preferredAgeMin in minAge..maxAge) && (sessionUser!!.preferredAgeMax in minAge..maxAge)) {
            ageRangeSlider!!.setValues(
                sessionUser!!.preferredAgeMin.toFloat(),
                sessionUser!!.preferredAgeMax.toFloat()
            )
        } else {
            ageRangeSlider!!.setValues(minAge.toFloat(), maxAge.toFloat())
        }

        if (sessionUser!!.preferredDistance in minDistance..maxDistance) {
            distanceSlider!!.value = sessionUser!!.preferredDistance.toFloat()
        } else {
            distanceSlider!!.value = 5F
        }

        saveChangesButton!!.setOnClickListener {
            sessionUser!!.preferredAgeMin = ageRangeSlider!!.values[0].toInt()
            sessionUser!!.preferredAgeMax = ageRangeSlider!!.values[1].toInt()
            sessionUser!!.preferredDistance = distanceSlider!!.value.toInt()

            userViewModel.updatePreferredParams(
                UserService.UpdatePreferredParamsBody(
                    sessionUser!!.email,
                    sessionUser!!.preferredAgeMin,
                    sessionUser!!.preferredAgeMax,
                    sessionUser!!.preferredDistance,
                )
            )
            userViewModel.method.observe(this) {
                UserSession.saveSession(this, sessionUser!!)
                Toast.makeText(applicationContext, "Changes saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        darkModeSwitch!!.setOnClickListener {
            if (darkModeSwitch!!.isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

}