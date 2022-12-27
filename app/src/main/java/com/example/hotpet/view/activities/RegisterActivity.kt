package com.example.hotpet.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.R
import com.example.hotpet.api.viewModels.UserViewModel
import com.example.hotpet.utils.AlertMaker
import com.google.android.material.textfield.TextInputEditText
import java.util.*


class RegisterActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var userViewModel: UserViewModel

    // VIEWS
    private var emailTIET: TextInputEditText? = null
    private var passwordTIET: TextInputEditText? = null
    private var usernameTIET: TextInputEditText? = null
    private var registerButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // VIEW BINDING
        emailTIET = findViewById(R.id.emailTIET)
        passwordTIET = findViewById(R.id.passwordTIET)
        usernameTIET = findViewById(R.id.usernameTIET)
        registerButton = findViewById(R.id.registerButton)

        registerButton!!.setOnClickListener { register() }
    }

    private fun register() {
        if (inputControl()) {
            val intent = Intent(this@RegisterActivity, CompleteRegistrationActivity::class.java)
            intent.putExtra("EMAIL", emailTIET!!.text.toString())
            intent.putExtra("PASSWORD", passwordTIET!!.text.toString())
            intent.putExtra("USERNAME", usernameTIET!!.text.toString())
            startActivity(intent)
        }
    }

    private fun inputControl(): Boolean {
        if (emailTIET!!.text.toString().isEmpty()) {
            AlertMaker.makeAlert(this, "Warning", "Email can't be empty")
            return false
        }
        if (passwordTIET!!.text.toString().isEmpty()) {
            AlertMaker.makeAlert(this, "Warning", "Password can't be empty")
            return false
        }
        if (usernameTIET!!.text.toString().isEmpty()) {
            AlertMaker.makeAlert(this, "Warning", "Username can't be empty")
            return false
        }
        return true
    }
}