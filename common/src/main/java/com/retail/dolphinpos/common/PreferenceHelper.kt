package com.retail.dolphinpos.common

import com.retail.dolphinpos.domain.model.auth.login.response.LoginData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceHelper @Inject constructor(
    private val preferenceManager: PreferenceManager
) {

    fun saveLoginData(loginData: LoginData, password: String) {
        preferenceManager.setStoreID(loginData.storeInfo.id)
        loginData.user.name?.let { preferenceManager.setName(it) }
        preferenceManager.setPassword(password)
        preferenceManager.setAccessToken(loginData.accessToken)
        preferenceManager.setRefreshToken(loginData.refreshToken)
        preferenceManager.setLogin(true)
    }

    fun getPassword(): String = preferenceManager.getPassword()
}