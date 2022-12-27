package com.example.hotpet.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.R
import com.example.hotpet.api.UserService
import com.example.hotpet.api.viewModels.UserViewModel
import com.example.hotpet.utils.AlertMaker
import com.google.android.material.textfield.TextInputEditText

class ForgotPasswordActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var userViewModel: UserViewModel

    // VIEWS
    private var emailTIET: TextInputEditText? = null
    private var nextBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // VIEW BINDING
        nextBtn = findViewById(R.id.nextBtn)
        emailTIET = findViewById(R.id.emailTIET)

        nextBtn!!.setOnClickListener {
            if (inputControl()) {
                userViewModel.forgotPassword(
                    UserService.ForgotPasswordBody(
                        emailTIET!!.text.toString()
                    )
                )
            }
        }

        setupObservers()
    }

    private fun setupObservers() {
        userViewModel.token.observe(this) {
            val intent = Intent(baseContext, ResetCodeActivity::class.java)
            intent.putExtra("TOKEN", it)
            intent.putExtra("EMAIL", emailTIET!!.text.toString())
            startActivity(intent)
            finish()
        }
        userViewModel.errorMessage.observe(this) {
            AlertMaker.makeAlert(this, "Error", it)
        }
    }

    private fun inputControl(): Boolean {
        if (emailTIET!!.text.toString().isEmpty()) {
            AlertMaker.makeAlert(this, "Warning", "Email can't be empty")
            return false
        }
        return true
    }
}