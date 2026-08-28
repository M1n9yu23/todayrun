package com.gyugle.gyurun.feature.settings.presentation.impl

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import timber.log.Timber

internal fun Context.openUrl(url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().build()
    try {
        customTabsIntent.launchUrl(this, Uri.parse(url))
    } catch (e: ActivityNotFoundException) {
        Timber.e(e, "링크를 열 브라우저가 없다: %s", url)
        Toast.makeText(this, R.string.about_open_link_failed, Toast.LENGTH_SHORT).show()
    }
}