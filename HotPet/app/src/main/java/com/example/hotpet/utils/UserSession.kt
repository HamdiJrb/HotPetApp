package com.example.hotpet.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.hotpet.api.models.User
import com.example.hotpet.api.viewModels.UserViewModel
import com.google.gson.Gson

object UserSession {

    fun checkSessionExist(context: Context): Boolean {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_SESSION, Activity.MODE_PRIVATE)
        return sharedPreferences.getString("USER_DATA", null) != null
    }

    fun getSession(context: Context): User {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_SESSION, Activity.MODE_PRIVATE)
        val userData = sharedPreferences.getString("USER_DATA", null)
        return Gson().fromJson(userData, User::class.java)!!
    }

    fun saveSession(context: Context, user: User) {
        val sharedPreferences =
            context.getSharedPreferences(Constants.SHARED_PREF_SESSION, Activity.MODE_PRIVATE)
        val sharedPreferencesEditor: SharedPreferences.Editor =
            sharedPreferences.edit()
        sharedPreferencesEditor.putString("USER_DATA", Gson().toJson(user))
        sharedPreferencesEditor.apply()
    }

    fun removeSession(context: Context) {
        val sharedPreferences =
            context.getSharedPreferences(
                Constants.SHARED_PREF_SESSION,
                Context.MODE_PRIVATE
            )
        val sharedPreferencesEditor: SharedPreferences.Editor = sharedPreferences.edit()
        sharedPreferencesEditor.clear().apply()
    }

    fun refreshSession(
        context: Context,
        viewModelStoreOwner: ViewModelStoreOwner,
        lifecycleOwner: LifecycleOwner
    ) {
        val userViewModel = ViewModelProvider(viewModelStoreOwner)[UserViewModel::class.java]

        try {
            userViewModel.getById(getSession(context)._id!!)
        } catch (e: NullPointerException) {
            removeSession(context)
        }
        userViewModel.user.observe(lifecycleOwner) {
            saveSession(context, it)
        }
    }
}