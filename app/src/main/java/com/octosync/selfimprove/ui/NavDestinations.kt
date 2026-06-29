package com.octosync.selfimprove.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavDestination : NavKey {
    @Serializable
    data object Home : NavDestination
    
    @Serializable
    data object History : NavDestination
}
