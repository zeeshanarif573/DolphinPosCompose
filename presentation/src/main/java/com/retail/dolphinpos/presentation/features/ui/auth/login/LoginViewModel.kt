package com.retail.dolphinpos.presentation.features.ui.auth.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retail.dolphinpos.common.PreferenceManager
import com.retail.dolphinpos.domain.model.auth.login.request.LoginRequest
import com.retail.dolphinpos.domain.model.auth.login.response.LoginData
import com.retail.dolphinpos.domain.repositories.auth.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val repository: LoginRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _loginUiEvent = MutableLiveData<LoginUiEvent>()
    val loginUiEvent: LiveData<LoginUiEvent> = _loginUiEvent

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginUiEvent.value = LoginUiEvent.ShowLoading
            try {
                val response = repository.login(LoginRequest(username, password))
                _loginUiEvent.value = LoginUiEvent.HideLoading

                response.loginData?.let { loginData ->
                    setPreferences(loginData, password)
                    loginData.storeInfo.logoUrl?.let {
                        loginData.storeInfo.locations?.let { locationsList ->
                            repository.insertUsersDataIntoLocalDB(
                                loginData.allStoreUsers,
                                loginData.storeInfo,
                                it,
                                locationsList,
                                preferenceManager.getPassword(),
                                loginData.user.id,
                                loginData.user.storeId,
                                loginData.user.locationId
                            )
                        }
                    }
                    _loginUiEvent.value = LoginUiEvent.NavigateToRegister

                } ?: run {
                    _loginUiEvent.value =
                        LoginUiEvent.ShowError(response.message ?: "No data received")
                }
            } catch (e: Exception) {
                _loginUiEvent.value = LoginUiEvent.HideLoading
                _loginUiEvent.value = LoginUiEvent.ShowError(e.message ?: "Something went wrong")
            }
        }
    }

    private fun setPreferences(loginData: LoginData, password: String) {
        preferenceManager.setStoreID(loginData.storeInfo.id)
        loginData.user.name?.let { preferenceManager.setName(it) }
        preferenceManager.setPassword(password)
        preferenceManager.setAccessToken(loginData.accessToken)
        preferenceManager.setRefreshToken(loginData.refreshToken)
        preferenceManager.setLogin(true)
    }
}