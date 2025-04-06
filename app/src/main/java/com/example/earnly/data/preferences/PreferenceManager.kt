package com.example.earnly.data.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCE_NAME, Context.MODE_PRIVATE
    )
    
    fun isFirstLaunch(): Boolean {
        return preferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    fun setFirstLaunchCompleted() {
        preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }
    
    fun getUserId(): String? {
        return preferences.getString(KEY_USER_ID, null)
    }
    
    fun setUserId(userId: String) {
        preferences.edit().putString(KEY_USER_ID, userId).apply()
    }
    
    companion object {
        private const val PREFERENCE_NAME = "earnly_preferences"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_USER_ID = "user_id"
    }
} 