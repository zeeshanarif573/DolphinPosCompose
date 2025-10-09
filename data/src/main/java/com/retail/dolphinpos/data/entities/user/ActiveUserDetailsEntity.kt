package com.retail.dolphinpos.data.entities.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_user_details")
data class ActiveUserDetailsEntity(
    @PrimaryKey
    val id: Int,
    val name: String? = "",
    val email: String? = "",
    val username: String? = "",
    val password: String? = "",
    val pin: String? = "",
    val userStatus: String? = "",
    val phoneNo: String? = "",
    val storeId: Int = 0,
    val locationId: Int = 0,
    val roleId: Int = 0,
    val roleTitle: String? = "",
    val storeName: String?,
    val address: String?,
    val storeMultiCashier: Boolean?,
    val policy: String?,
    val advertisementImg: String?,
    val isAdvertisement: Int?,
    val alt: String?,
    val original: String?,
    val thumbnail: String?,
    val locationName: String?,
    val locationAddress: String?,
    val locationStatus: String?,
    val zipCode: String?,
    val taxValue: String?,
    val taxTitle: String?,
    val startTime: String?,
    val endTime: String?,
    val locationMultiCashier: Boolean?,
    val registerId: Int,
    val registerName: String?,
    val registerStatus: String?,
)