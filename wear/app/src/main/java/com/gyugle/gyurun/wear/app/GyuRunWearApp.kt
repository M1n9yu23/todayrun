package com.gyugle.gyurun.wear.app

import android.app.Application
import androidx.core.content.ContextCompat
import com.gyugle.gyurun.wear.app.di.wearAppModules
import com.gyugle.gyurun.wear.app.exercise.ExerciseService
import com.gyugle.gyurun.wear.app.exercise.ExerciseServiceCommand
import com.gyugle.gyurun.wear.app.exercise.exerciseServiceCommandFor
import com.gyugle.gyurun.wear.app.tile.RunTileService
import com.gyugle.gyurun.wear.run.domain.RunningTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import timber.log.Timber

class GyuRunWearApp :
    Application(),
    KoinComponent {
    private val applicationScope: CoroutineScope by inject()
    private val runningTracker: RunningTracker by inject()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@GyuRunWearApp)
            modules(wearAppModules)
        }

        Timber.i("GyuRun 워치 시작 — DI 컨테이너 준비 완료")

        runningTracker.isTracking
            .onEach { isTracking ->
                RunTileService.requestUpdate(this)
                val command =
                    exerciseServiceCommandFor(
                        isTracking = isTracking,
                        isServiceRunning = runningTracker.isExerciseServiceRunning.value,
                    )
                when (command) {
                    ExerciseServiceCommand.START -> {
                        ContextCompat.startForegroundService(
                            this,
                            ExerciseService.createStartIntent(this),
                        )
                    }

                    ExerciseServiceCommand.STOP -> {
                        startService(ExerciseService.createStopIntent(this))
                    }

                    ExerciseServiceCommand.NONE -> {
                        Unit
                    }
                }
            }.launchIn(applicationScope)
    }
}
