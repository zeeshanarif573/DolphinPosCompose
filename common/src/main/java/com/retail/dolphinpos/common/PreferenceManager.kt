package com.retail.dolphinpos.common

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dolphin_prefs", Context.MODE_PRIVATE)

    fun setRegister(value: Boolean) {
        prefs.edit { putBoolean(Constants.SET_REGISTER, value) }
    }

    fun getRegister(defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(Constants.SET_REGISTER, defaultValue)
    }

    fun setLogin(value: Boolean) {
        prefs.edit { putBoolean(Constants.IS_LOGIN, value) }
    }

    fun isLogin(defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(Constants.IS_LOGIN, defaultValue)
    }

    fun setAccessToken(value: String) {
        prefs.edit { putString(Constants.ACCESS_TOKEN, value) }
    }

    fun getAccessToken(defaultValue: String = ""): String {
        return prefs.getString(Constants.ACCESS_TOKEN, defaultValue) ?: defaultValue
    }

    fun setRefreshToken(value: String) {
        prefs.edit { putString(Constants.REFRESH_TOKEN, value) }
    }

    fun getRefreshToken(defaultValue: String = ""): String {
        return prefs.getString(Constants.REFRESH_TOKEN, defaultValue) ?: defaultValue
    }

    fun setUserID(value: Int) {
        prefs.edit { putInt(Constants.USER_ID, value) }
    }

    fun getUserID(defaultValue: Int = 0): Int {
        return prefs.getInt(Constants.USER_ID, defaultValue)
    }

    fun setStoreID(value: Int) {
        prefs.edit { putInt(Constants.STORE_ID, value) }
    }

    fun getStoreID(defaultValue: Int = 0): Int {
        return prefs.getInt(Constants.STORE_ID, defaultValue)
    }

    fun setName(value: String) {
        prefs.edit { putString(Constants.NAME, value) }
    }

    fun getName(defaultValue: String = ""): String {
        return prefs.getString(Constants.NAME, defaultValue) ?: defaultValue
    }

    fun setPassword(value: String) {
        prefs.edit { putString(Constants.PASSWORD, value) }
    }

    fun getPassword(defaultValue: String = ""): String {
        return prefs.getString(Constants.PASSWORD, defaultValue) ?: defaultValue
    }

    fun setOccupiedLocationID(value: Int) {
        prefs.edit { putInt(Constants.OCCUPIED_LOCATION_ID, value) }
    }

    fun getOccupiedLocationID(defaultValue: Int = 0): Int {
        return prefs.getInt(Constants.OCCUPIED_LOCATION_ID, defaultValue)
    }

    fun setOccupiedRegisterID(value: Int) {
        prefs.edit { putInt(Constants.OCCUPIED_REGISTER_ID, value) }
    }

    fun getOccupiedRegisterID(defaultValue: Int = 0): Int {
        return prefs.getInt(Constants.OCCUPIED_REGISTER_ID, defaultValue)
    }

}