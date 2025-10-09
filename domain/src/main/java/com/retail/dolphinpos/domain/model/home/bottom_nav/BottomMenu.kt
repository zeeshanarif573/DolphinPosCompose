package com.retail.dolphinpos.domain.model.home.bottom_nav

import androidx.annotation.IdRes

data class BottomMenu(
    val menuName: String,
    @IdRes val destinationId: Int? = null,
    val activityClass: Class<*>? = null,
    val action: MenuAction
)

enum class MenuAction {
    NAV_DESTINATION,
    OPEN_ACTIVITY
}
