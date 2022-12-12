package com.example.secureFolderManagement

import android.content.SharedPreferences

class PreferenceManager(sp: SharedPreferences){

    private val sharedPref: SharedPreferences = sp

    fun checkFirstTime(): Boolean {
        if (!sharedPref.getBoolean("firstTime", false)) {
            setFirstTime()
            return false
        }
        return true
    }
    fun setFirstTime() {
        sharedPref.edit().putBoolean("firstTime", true).apply()
    }

    fun checkPINflag(): Boolean {
        return sharedPref.getBoolean("hasPIN", false)
    }
    fun setPINflag() {
        sharedPref.edit().putBoolean("hasPIN", true).apply()
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

    fun writeUsername(username: String){
        val editor = sharedPref.edit()
        editor.putString("username", username)
        editor.apply()
    }


}