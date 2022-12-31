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

class ResetCodeActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var userViewModel: UserViewModel

    // VIEWS
    private var resetCodeTIET: TextInputEditText? = null
    private var nextBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_code)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // VIEW BINDING
        resetCodeTIET = findViewById(R.id.resetCodeTIET)
        nextBtn = findViewById(R.id.nextBtn)

        nextBtn!!.setOnClickListener {
            if (inputControl()) {
                userViewModel.verifyResetCode(
                    UserService.VerifyResetCodeBody(
                        resetCodeTIET!!.text.toString(),
                        intent.getStringExtra("TOKEN")!!
                    )
                )
            }
        }
        setupObservers()
    }

    private fun setupObservers() {
        userViewModel.method.observe(this) {
            if (it.equals("verifyResetCode")) {
                val newIntent = Intent(baseContext, NewPasswordActivity::class.java)
                intent.putExtra("EMAIL", intent.getStringExtra("EMAIL")!!)
                startActivity(newIntent)
                finish()
            }
        }
        userViewModel.errorMessage.observe(this) {
            AlertMaker.makeAlert(this, "Error", it)
        }
    }

    private fun inputControl(): Boolean {
        if (resetCodeTIET!!.text.toString().isEmpty()) {
            AlertMaker.makeAlert(this, "Warning", "Reset code can't be empty")
            return false
        }
        return true
    }
}