package tn.esprit.smartpet.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartpet.R
import tn.esprit.smartpet.api.ApiInterface
import tn.esprit.smartpet.model.User

const val PREF_NAME = "PREF_PET"
/*const val EMAIL = "EMAIL"
const val PASSWORD = "PASSWORD"*/
const val IS_REMEMBRED = "IS_REMEMBRED"

class Signin : AppCompatActivity() {

    lateinit var Email: EditText
    lateinit var layoutEmail: TextInputLayout
    lateinit var Pwd: EditText
    lateinit var layoutPwd: TextInputLayout

    lateinit var forgotYourPassword : TextView
    lateinit var RememberMe: CheckBox

    //TODO 1 "Declare a var of SharedPreferences"
    lateinit var mSharedPref: SharedPreferences

    var call = ApiInterface.create() // call

    private lateinit var btn1_sign_in: Button
    private lateinit var btn1_sign_up: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        supportActionBar?.hide()

        Email = findViewById(R.id.Email)
        layoutEmail = findViewById(R.id.layoutEmail)

        Pwd = findViewById(R.id.Pwd)
        layoutPwd = findViewById(R.id.layoutPwd)

        RememberMe = findViewById(R.id.Remember_Me)
        forgotYourPassword = findViewById(R.id.forgot)

        //TODO 2 "Initialize the var of SharedPreferences"
        mSharedPref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        btn1_sign_up = findViewById(R.id.btn1_sign_up)
        btn1_sign_up.setOnClickListener{
            val mainIntent = Intent(this, Signup::class.java)
            startActivity(mainIntent)
        }

        btn1_sign_in = findViewById(R.id.btn1_sign_in)

        /*//TODO 3 "Test in the SharedPreferences if there's data"
        if (mSharedPref.getBoolean(IS_REMEMBRED, false)){
            val mainIntent = Intent(this, Home::class.java)
            startActivity(mainIntent)
        }*/

        btn1_sign_in.setOnClickListener()
        {
            signIn()
        }
    }

    private fun signIn()
    {
        if (validate())
        {
            val user = User( email=Email.text.toString(), password=Pwd.text.toString())
            call.login(user).enqueue(object : Callback<User>
            {
                override fun onResponse(call: Call<User>, response: Response<User>)
                {
                    //TODO 4 "Edit the SharedPreferences by putting all the data"
                    if (RememberMe.isChecked)
                    {
                        mSharedPref.edit().apply{
                            putBoolean(IS_REMEMBRED, true)
                        }.apply()
                        val intent = Intent(this@Signin, InformationActivity::class.java)
                        startActivity(intent)
                    }
                    if (response.isSuccessful)
                    {
                        val editor = mSharedPref.edit()
                        editor.putString("EMAIL", Email.text.toString())
                        editor.apply()

                        val intent = Intent(this@Signin, InformationActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this@Signin, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable)
                {
                    Log.d("Error", t.message.toString())
                }
            })
        }
    }

    private fun validate():Boolean
    {
        var email:Boolean=true
        var pwd:Boolean=true

        layoutEmail?.error =null
        layoutPwd?.error =null

        if(Email?.text!!.isEmpty())
        {
            layoutEmail?.error="Please enter your email !"
            email=false
        }
        if(Pwd?.text!!.isEmpty())
        {
            layoutPwd?.error="Please enter your password !"
            pwd=false
        }

        if (pwd===false || email===false)
        {
            return false
        }
        return true
    }
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}