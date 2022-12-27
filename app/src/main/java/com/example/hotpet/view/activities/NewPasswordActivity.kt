package com.example.hotpet.view.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.R
import com.example.hotpet.api.UserService
import com.example.hotpet.api.viewModels.UserViewModel
import com.example.hotpet.utils.AlertMaker
import com.google.android.material.textfield.TextInputEditText

class NewPasswordActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var userViewModel: UserViewModel

    // VIEWS
    var passwordConfirmationTIET: TextInputEditText? = null
    var passwordTIET: TextInputEditText? = null
    var nextBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // VIEW BINDING
        passwordConfirmationTIET = findViewById(R.id.passwordConfirmationTIET)
        passwordTIET = findViewById(R.id.passwordTIET)
        nextBtn = findViewById(R.id.nextBtn)

        val email = intent.getStringExtra("EMAIL")

        nextBtn!!.setOnClickListener {
            if (inputControl()) {
                userViewModel.updatePassword(
                    UserService.UpdatePasswordBody(
                        email!!,
                        passwordTIET!!.text.toString()
                    )
                )
                userViewModel.method.observe(this) {
                    if (it.equals("updatePassword")) {
                        Toast.makeText(
                            baseContext,
                            "Password changed successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
                userViewModel.errorMessage.observe(this) {
                    AlertMaker.makeAlert(this, "Error", it)
                }
            }
        }
    }

    private fun inputControl(): Boolean {
        if (passwordTIET!!.text.toString().isEmpty()) {
            AlertMaker.makeAlert(this, "Warning", "Password can't be empty")
            return false
        }
        if (passwordConfirmationTIET!!.text.toString().isEmpty()) {
            AlertMaker.makeAlert(this, "Warning", "Password confirmation can't be empty")
            return false
        }
        if (passwordTIET!!.text.toString() != passwordConfirmationTIET!!.text.toString()) {
            AlertMaker.makeAlert(this, "Warning", "Password and confirmation don't match")
            return false
        }
        return true
    }
}