package com.example.secureFolderManagement

import android.content.SharedPreferences

class PreferenceManager(sp: SharedPreferences){

    private val sharedPref: SharedPreferences = sp



    fun checkOnboarding(): Boolean {
        if (!sharedPref.getBoolean("isOnboarded", false)) {
            return false
        }
        return true
    }
    fun setOnboarding() {
        sharedPref.edit().putBoolean("isOnboarded", true).apply()
    }

    fun checkAuth(): Boolean {
        if (!sharedPref.getBoolean("isAuth", false)) {
            return false
        }
        return true
    }
    fun setAuth() {
        sharedPref.edit().putBoolean("isAuth", true).apply()
    }

    fun checkPINflag(): Boolean {
        return sharedPref.getBoolean("isPinSet", false)
    }
    fun setPINflag(boolean: Boolean) {
        sharedPref.edit().putBoolean("isPinSet", boolean).apply()
    }

    fun getPIN(): String {
        return sharedPref.getString("PIN", "-1").toString()
    }
    fun setPIN(pin:String) {
        sharedPref.edit().putString("PIN", pin).apply()
    }

    fun checkUsername(): String? {
        return sharedPref.getString("username", "")
    }

    fun setUsername(username: String){
        val editor = sharedPref.edit()
        editor.putString("username", username)
        editor.apply()
    }


}