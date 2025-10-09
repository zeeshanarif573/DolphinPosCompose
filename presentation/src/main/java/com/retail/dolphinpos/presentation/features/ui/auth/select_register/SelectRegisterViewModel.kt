package com.retail.dolphinpos.presentation.features.ui.auth.select_register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.retail.dolphinpos.common.PreferenceManager
import com.retail.dolphinpos.domain.model.auth.select_registers.request.UpdateStoreRegisterRequest
import com.retail.dolphinpos.domain.model.home.catrgories_products.Category
import com.retail.dolphinpos.domain.model.home.catrgories_products.CategoryData
import com.retail.dolphinpos.domain.model.home.catrgories_products.Products
import com.retail.dolphinpos.domain.repositories.auth.StoreRegistersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectRegisterViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val storeRegistersRepository: StoreRegistersRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _selectRegisterUiEvent = MutableLiveData<SelectRegisterUiEvent>()
    val selectRegisterUiEvent: LiveData<SelectRegisterUiEvent> = _selectRegisterUiEvent


    init {
        getStoreLocations()
    }

    fun getStoreLocations() {
        viewModelScope.launch {
            _selectRegisterUiEvent.value = SelectRegisterUiEvent.ShowLoading
            try {
                val response = storeRegistersRepository.getLocations(preferenceManager.getStoreID())
                _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                if (response.isNotEmpty()) {
                    _selectRegisterUiEvent.value =
                        SelectRegisterUiEvent.PopulateLocationsList(response)
                } else
                    SelectRegisterUiEvent.ShowError("No Location Found")

            } catch (e: Exception) {
                _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                _selectRegisterUiEvent.value =
                    SelectRegisterUiEvent.ShowError(e.message ?: "Something went wrong")
            }
        }
    }

    fun getStoreRegisters(locationID: Int) {
        viewModelScope.launch {
            _selectRegisterUiEvent.value = SelectRegisterUiEvent.ShowLoading
            try {
                val response =
                    storeRegistersRepository.getRegistersByLocationID(locationID)
                _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                if (response.isNotEmpty()) {
                    _selectRegisterUiEvent.value =
                        SelectRegisterUiEvent.PopulateRegistersList(response)
                } else
                    SelectRegisterUiEvent.ShowError("No Registers Found")

            } catch (e: Exception) {
                _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                _selectRegisterUiEvent.value =
                    SelectRegisterUiEvent.ShowError(e.message ?: "Something went wrong")
            }
        }
    }

    fun updateStoreRegister(locationID: Int, storeRegisterID: Int) {
        viewModelScope.launch {
            _selectRegisterUiEvent.value = SelectRegisterUiEvent.ShowLoading
            try {
                val response = storeRegistersRepository.updateStoreRegister(
                    UpdateStoreRegisterRequest(
                        preferenceManager.getStoreID(),
                        locationID,
                        storeRegisterID
                    )
                )
                response.message?.let {
                    preferenceManager.setRegister(true)
                    preferenceManager.setOccupiedLocationID(locationID)
                    preferenceManager.setOccupiedRegisterID(storeRegisterID)
                    storeRegistersRepository.insertRegisterStatusDetailsIntoLocalDB(response.data)
                    _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                    _selectRegisterUiEvent.value = SelectRegisterUiEvent.NavigateToPinScreen
                } ?: run {
                    _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                }

            } catch (e: Exception) {
                _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                _selectRegisterUiEvent.value =
                    SelectRegisterUiEvent.ShowError(e.message ?: "Something went wrong")
            }
        }
    }

    fun getProducts(locationID: Int, storeRegisterID: Int) {
        viewModelScope.launch {
            try {
                val response =
                    storeRegistersRepository.getProducts(preferenceManager.getStoreID(), locationID)
                if (response.category?.categories.isNullOrEmpty()) {
                    _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                    _selectRegisterUiEvent.value =
                        SelectRegisterUiEvent.ShowError("No categories found in response")
                    return@launch
                }

                response.category!!.categories.forEach { category ->
                    // Insert category
                    storeRegistersRepository.insertCategoriesIntoLocalDB(listOf(category))
                    category.products.forEach { product ->
                        // Insert products
                        storeRegistersRepository.insertProductsIntoLocalDB(
                            listOf(product),
                            category.id
                        )
                        // Insert product images
                        storeRegistersRepository.insertProductImagesIntoLocalDB(
                            product.images,
                            product.id
                        )
                        // Insert vendor
                        product.vendor?.let {
                            storeRegistersRepository.insertVendorDetailsIntoLocalDB(it, product.id)
                        }
                        // Insert variants
                        product.variants.forEach { variant ->
                            storeRegistersRepository.insertProductVariantsIntoLocalDB(
                                listOf(variant),
                                product.id
                            )
                            // Insert variant images
                            storeRegistersRepository.insertVariantImagesIntoLocalDB(
                                variant.images,
                                variant.id
                            )
                        }
                    }
                }
                _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                updateStoreRegister(locationID, storeRegisterID)

            } catch (e: Exception) {
                _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                _selectRegisterUiEvent.value =
                    SelectRegisterUiEvent.ShowError(e.message ?: "Something went wrong")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _selectRegisterUiEvent.value = SelectRegisterUiEvent.ShowLoading
            try {
                val response = storeRegistersRepository.logout()
                response.message.let {
                    preferenceManager.setLogin(false)
                    _selectRegisterUiEvent.value = SelectRegisterUiEvent.NavigateToLoginScreen
                }
                _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading

            } catch (e: Exception) {
                _selectRegisterUiEvent.value = SelectRegisterUiEvent.HideLoading
                _selectRegisterUiEvent.value =
                    SelectRegisterUiEvent.ShowError(e.message ?: "Something went wrong")
            }
        }
    }

    fun getName(): String {
        return preferenceManager.getName()
    }
}