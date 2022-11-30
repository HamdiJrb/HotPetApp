package tn.esprit.smartpet.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import tn.esprit.smartpet.R
import tn.esprit.smartpet.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    private lateinit var mSharedPref: SharedPreferences

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(DiscoverFragment())
        mSharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)


        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.discoverFragment -> replaceFragment(DiscoverFragment())

                /*R.id.matchFragment -> replaceFragment(MatchFragment())*/

                R.id.chatFragment -> replaceFragment(ChatFragment())

                R.id.profileFragment -> {
                    /*ProfileFragment.newInstance(
                        mSharedPref.getString(EMAIL, "").toString())*/
                        /*intent.getStringExtra(AGE).toString(),
                        intent.getStringExtra(IMAGE).toString())*/
                    replaceFragment(ProfileFragment())
                }
                else -> {
                }
            }
                true
            }

        }

    private fun replaceFragment (fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

        override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}