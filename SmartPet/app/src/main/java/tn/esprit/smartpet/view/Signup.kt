package tn.esprit.smartpet.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tn.esprit.smartpet.R
import tn.esprit.smartpet.api.ApiInterface
import tn.esprit.smartpet.model.User

class Signup : AppCompatActivity() {

    var calll = ApiInterface.create()  // call

    lateinit var Email: TextInputEditText
    lateinit var layoutEmail: TextInputLayout

    lateinit var Name: TextInputEditText
    lateinit var layoutName: TextInputLayout

    lateinit var Pwd: TextInputEditText
    lateinit var layoutPwd: TextInputLayout

    lateinit var btn2_sign_up: Button
    lateinit var btn2_sign_in: Button

    lateinit var mSharedPref: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        mSharedPref=getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        Email = findViewById(R.id.Email)
        layoutEmail = findViewById(R.id.layoutEmail)

        Name = findViewById(R.id.Name)
        layoutName = findViewById(R.id.layoutName)

        Pwd = findViewById(R.id.Pwd)
        layoutPwd = findViewById(R.id.layoutPwd)

        btn2_sign_up = findViewById(R.id.btn2_sign_up)
        btn2_sign_up.setOnClickListener{
            val mainIntent = Intent(this, Signin::class.java)
            startActivity(mainIntent)
        }

        btn2_sign_in = findViewById(R.id.btn2_sign_in)

        btn2_sign_in.setOnClickListener{
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }

        btn2_sign_up.setOnClickListener{
            signUp()
        }
    }
// fct signup
    private fun signUp()
    {
        if (validate())
        {
            val user = User(email=Email.text.toString(), password=Pwd.text.toString(),username=Name.text.toString())
            calll.signup(user).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@Signup,
                            "User added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        val editor = mSharedPref.edit()
                        editor.putString("EMAIL", Email.text.toString())
                        editor.apply()

                        val intent = Intent(this@Signup, InformationActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(
                            this@Signup,
                            "User already exists",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable)
                {
                    Toast.makeText(this@Signup, "Ooooops !! ", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

// fct validate
private fun validate(): Boolean {
    var name=true
    var email=true
    var pwd=true

    layoutName?.error =null
    layoutEmail?.error =null
    layoutPwd?.error =null

    if(Name?.text!!.isEmpty())
    {
        layoutName?.error="Please enter your username!"
        name=false
    }

    if(Email?.text!!.isEmpty())
    {
        layoutEmail?.error="Please enter your e-mail!"
        email=false
    }

    if(Pwd?.text!!.isEmpty())
    {
        layoutPwd?.error="Please enter your password !"
        pwd=false
    }

    if(!Patterns.EMAIL_ADDRESS.matcher(Email?.text!!).matches() && Name?.text!!.isNotEmpty())
    {

        layoutEmail?.error="Email not valid !"
        email= false
    }
    if (name===false || email===false||pwd===false )
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