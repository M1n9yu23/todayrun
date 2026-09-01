package com.gyugle.gyurun.run.location

import android.content.Context
import com.gyugle.gyurun.run.domain.ActiveRunServiceController

internal class AndroidActiveRunServiceController(
    private val context: Context,
) : ActiveRunServiceController {
    override fun stop() {
        if (!ActiveRunService.isServiceActive) return
        context.startService(ActiveRunService.createStopIntent(context))
    }
}
