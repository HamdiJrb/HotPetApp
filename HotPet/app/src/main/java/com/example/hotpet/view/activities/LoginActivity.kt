package com.example.hotpet.view.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.hotpet.R
import com.example.hotpet.api.UserService
import com.example.hotpet.api.viewModels.UserViewModel
import com.example.hotpet.utils.AlertMaker
import com.example.hotpet.utils.UserSession
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException


class LoginActivity : AppCompatActivity() {

    // VARIABLES
    private lateinit var userViewModel: UserViewModel
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager
    private val requestCodeGoogleSignIn = 105

    // VIEWS
    private var emailTIET: TextInputEditText? = null
    private var passwordTIET: TextInputEditText? = null
    private var loginButton: Button? = null
    private var signupButton: Button? = null
    private var googleLoginButton: SignInButton? = null
    private var facebookLoginButton: LoginButton? = null
    private var forgotPasswordButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // VIEW BINDING
        emailTIET = findViewById(R.id.emailTIET)
        passwordTIET = findViewById(R.id.passwordTIET)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)
        googleLoginButton = findViewById(R.id.googleLoginButton)
        facebookLoginButton = findViewById(R.id.facebookLoginButton)
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton)

        loginButton!!.setOnClickListener { login() }

        signupButton!!.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        setupObservers()
        setupGoogleSignInButton()
        setupFacebookSignInButton()

        forgotPasswordButton!!.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
    }

    private fun setupObservers() {
        userViewModel.user.observe(this) {
            UserSession.saveSession(this@LoginActivity, it)
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
        userViewModel.errorMessage.observe(this) {
            AlertMaker.makeAlert(this, "Error", it)
        }
    }

    private fun login() {
        if (inputControl()) {
            userViewModel.login(
                UserService.LoginBody(
                    emailTIET!!.text.toString(),
                    passwordTIET!!.text.toString()
                )
            )
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
        return true
    }

    private fun setupGoogleSignInButton() {
        googleLoginButton!!.setSize(SignInButton.SIZE_STANDARD)
        googleLoginButton!!.setOnClickListener {
            val gso: GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
            val signInIntent: Intent = GoogleSignIn.getClient(this, gso).signInIntent
            @Suppress("DEPRECATION")
            startActivityForResult(signInIntent, requestCodeGoogleSignIn)
        }

    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestCodeGoogleSignIn) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    loginWithSocialMedia(account.email!!)
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupFacebookSignInButton() {

        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(this)
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()

        @Suppress("DEPRECATION")
        facebookLoginButton!!.setReadPermissions(listOf("email"))
        facebookLoginButton!!.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    println("Facebook login success")
                    println(result)
                    if (AccessToken.getCurrentAccessToken() != null) {
                        val request = GraphRequest.newMeRequest(
                            AccessToken.getCurrentAccessToken()
                        ) { _, response ->
                            val json = response!!.getJSONObject()
                            try {
                                if (json != null) loginWithSocialMedia(json.getString("email"))
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,link,email,picture")
                        request.parameters = parameters
                        request.executeAsync()
                    }
                    loginManager.logOut()
                }

                override fun onCancel() {
                    println("Facebook login canceled")
                }

                override fun onError(error: FacebookException) {
                    println("Facebook login error")
                    error.printStackTrace()
                }
            }
        )
    }

    private fun loginWithSocialMedia(email: String) {
        println("Sign in : $email")
        userViewModel.loginWithSocial(UserService.LoginWithSocialBody(email))
        userViewModel.user.observe(this) {
            UserSession.saveSession(this@LoginActivity, it)
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }
}