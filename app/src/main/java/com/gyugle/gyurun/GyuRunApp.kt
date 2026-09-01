package com.gyugle.gyurun

import android.app.Application
import androidx.glance.appwidget.updateAll
import com.gyugle.gyurun.connectivity.data.di.connectivityModule
import com.gyugle.gyurun.core.common.DispatcherProvider
import com.gyugle.gyurun.core.common.di.coreCommonModule
import com.gyugle.gyurun.core.data.di.dataModule
import com.gyugle.gyurun.core.database.di.databaseModule
import com.gyugle.gyurun.core.datastore.di.dataStoreModule
import com.gyugle.gyurun.core.domain.preferences.repository.UserSettingsRepository
import com.gyugle.gyurun.core.domain.run.repository.RunRepository
import com.gyugle.gyurun.core.network.di.networkModule
import com.gyugle.gyurun.di.appModule
import com.gyugle.gyurun.feature.active.presentation.impl.di.activePresentationModule
import com.gyugle.gyurun.feature.details.presentation.impl.di.detailsPresentationModule
import com.gyugle.gyurun.feature.onboarding.presentation.impl.di.onboardingPresentationModule
import com.gyugle.gyurun.feature.overview.presentation.impl.di.overviewPresentationModule
import com.gyugle.gyurun.feature.settings.presentation.impl.di.settingsPresentationModule
import com.gyugle.gyurun.feature.stats.presentation.impl.di.statsPresentationModule
import com.gyugle.gyurun.initializer.initializeDebugTools
import com.gyugle.gyurun.reminder.WeeklySummaryWorker
import com.gyugle.gyurun.run.location.di.locationModule
import com.gyugle.gyurun.run.sensor.di.sensorModule
import com.gyugle.gyurun.widget.WeeklyStatsWidget
import com.gyugle.gyurun.widget.WidgetLanguage
import com.gyugle.gyurun.widget.WidgetRefresher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import timber.log.Timber

class GyuRunApp :
    Application(),
    KoinComponent {
    override fun onCreate() {
        super.onCreate()

        initializeDebugTools()

        startKoin {
            androidLogger()
            androidContext(this@GyuRunApp)
            workManagerFactory()
            modules(
                coreCommonModule,
                networkModule,
                databaseModule,
                dataStoreModule,
                dataModule,
                locationModule,
                sensorModule,
                connectivityModule,
                onboardingPresentationModule,
                overviewPresentationModule,
                activePresentationModule,
                detailsPresentationModule,
                statsPresentationModule,
                settingsPresentationModule,
                appModule,
            )
        }

        Timber.i("GyuRun 시작 - DI 컨테이너 준비 완료")

        WeeklySummaryWorker.schedule(this)
        startWidgetRefresher()
    }

    fun useLanguageForWidget(languageTags: String) {
        get<WidgetLanguage>().set(languageTags)
        get<CoroutineScope>().launch {
            WeeklyStatsWidget().updateAll(this@GyuRunApp)
        }
    }

    private fun startWidgetRefresher() {
        val applicationScope = get<CoroutineScope>()
        applicationScope.launch(get<DispatcherProvider>().io) {
            WidgetRefresher(
                runsChanged = get<RunRepository>().getMostRecentRun(),
                distanceUnit = get<UserSettingsRepository>().userSettings.map { it.distanceUnit },
                applicationScope = applicationScope,
                redrawWidget = { WeeklyStatsWidget().updateAll(this@GyuRunApp) },
            ).start()
        }
    }
}
