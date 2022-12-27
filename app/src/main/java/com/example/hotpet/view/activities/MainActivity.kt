package com.example.hotpet.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.hotpet.R
import com.example.hotpet.utils.UserSession
import com.example.hotpet.view.fragments.ConversationsFragment
import com.example.hotpet.view.fragments.MatchesFragment
import com.example.hotpet.view.fragments.DiscoverFragment
import com.example.hotpet.view.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var bottomNavigation: BottomNavigationView? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UserSession.refreshSession(this, this, this)

        // VIEW BINDING
        toolbar = findViewById(R.id.my_toolbar)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        setSupportActionBar(toolbar!!)

        bottomNavigation!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.discoverFragment -> {
                    supportActionBar?.title = "Discover"
                    setFragment(DiscoverFragment())
                    true
                }
                R.id.matchFragment -> {
                    supportActionBar?.title = "Your matches"
                    setFragment(MatchesFragment())
                    true
                }
                R.id.chatFragment -> {
                    supportActionBar?.title = "Messaging"
                    setFragment(ConversationsFragment())
                    true
                }
                R.id.profileFragment -> {
                    supportActionBar?.title = "Your profile"
                    setFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        setFragment(DiscoverFragment())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onBackPressed() {}
}