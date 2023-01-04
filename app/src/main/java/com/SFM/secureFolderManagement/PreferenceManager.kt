package com.SFM.secureFolderManagement

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

    fun logout() {
        sharedPref.edit().putBoolean("isAuth", false).apply()
        sharedPref.edit().putBoolean("isPinSet", false).apply()
    }

    fun unsetAuth() {
        sharedPref.edit().putBoolean("isAuth", false).apply()
        sharedPref.edit().remove("username").apply()
    }

    fun setAuth(username: String) {
        sharedPref.edit().putBoolean("isAuth", true).apply()
        sharedPref.edit().putString("username", username).apply()
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

    fun setUserGroupSettings(usergroup: String,
                             minPass: Int,
                             requireBiometrics: Boolean,
                             requireEncryption: Boolean,
                             pinType: String,
                             companyID:String,
                             pinMaxTries: Int,
                             pinLockoutTime: Int){
        val editor = sharedPref.edit()
        editor.putString("usergroup", usergroup)
        editor.putInt("minPass", minPass)
        editor.putString("usergroup", usergroup)
        editor.putString("companyID", companyID)
        editor.putBoolean("requireEncryption", requireEncryption)
        editor.putBoolean("requireBiometris", requireBiometrics)
        editor.putString("pinType", pinType)
        editor.putInt("pinMaxTries", pinMaxTries)
        editor.putInt("pinLockoutTime", pinLockoutTime)
        editor.putBoolean("isPinSet", false)
        editor.apply()

    }


}