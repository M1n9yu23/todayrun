package com.gyugle.gyurun.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

class Navigator {
    lateinit var backStack: NavBackStack<NavKey>

    fun navigateTo(key: NavKey) {
        backStack.add(key)
    }

    fun navigateBack() {
        backStack.removeLastOrNull()
    }
}