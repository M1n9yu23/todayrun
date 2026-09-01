package com.gyugle.gyurun.wear.app.tile

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textEdgeButton
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.ListenableFuture
import com.gyugle.gyurun.wear.app.MainActivity
import com.gyugle.gyurun.wear.app.R
import com.gyugle.gyurun.wear.designsystem.theme.WearColorScheme
import com.gyugle.gyurun.wear.run.domain.RunningTracker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val RESOURCES_VERSION = "1"

private val GyuRunTileColors = tileColorScheme(WearColorScheme)

internal fun tileStatusRes(isRunning: Boolean): Int = if (isRunning) R.string.wear_tile_status_running else R.string.wear_tile_status_ready

internal fun tileButtonRes(isRunning: Boolean): Int = if (isRunning) R.string.wear_tile_open else R.string.wear_tile_start

class RunTileService :
    TileService(),
    KoinComponent {
    private val runningTracker: RunningTracker by inject()

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<Tile> {
        val isRunning = runningTracker.isTracking.value

        val tile =
            Tile
                .Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        tileLayout(requestParams.deviceConfiguration, isRunning),
                    ),
                ).build()

        return CallbackToFutureAdapter.getFuture { completer ->
            completer.set(tile)
            "onTileRequest"
        }
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<Resources> =
        CallbackToFutureAdapter.getFuture { completer ->
            completer.set(
                Resources
                    .Builder()
                    .setVersion(RESOURCES_VERSION)
                    .build(),
            )
            "onTileResourcesRequest"
        }

    private fun tileLayout(
        deviceConfiguration: DeviceParameters,
        isRunning: Boolean,
    ): LayoutElement =
        materialScope(
            context = this,
            deviceConfiguration = deviceConfiguration,
            allowDynamicTheme = false,
            defaultColorScheme = GyuRunTileColors,
        ) {
            primaryLayout(
                titleSlot = {
                    text(getString(R.string.wear_app_name).layoutString)
                },
                mainSlot = {
                    text(getString(tileStatusRes(isRunning)).layoutString)
                },
                bottomSlot = {
                    textEdgeButton(
                        onClick = launchAppClickable(),
                        labelContent = {
                            text(getString(tileButtonRes(isRunning)).layoutString)
                        },
                    )
                },
            )
        }

    private fun launchAppClickable(): Clickable =
        Clickable
            .Builder()
            .setId("open_gyurun")
            .setOnClick(
                ActionBuilders.LaunchAction
                    .Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity
                            .Builder()
                            .setPackageName(packageName)
                            .setClassName(MainActivity::class.java.name)
                            .build(),
                    ).build(),
            ).build()

    companion object {
        fun requestUpdate(context: Context) {
            getUpdater(context).requestUpdate(RunTileService::class.java)
        }
    }
}
