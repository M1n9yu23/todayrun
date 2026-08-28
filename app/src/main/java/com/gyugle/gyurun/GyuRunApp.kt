package com.gyugle.gyurun

import android.app.Application
import com.gyugle.gyurun.core.common.di.coreCommonModule
import com.gyugle.gyurun.core.data.di.dataModule
import com.gyugle.gyurun.core.database.di.databaseModule
import com.gyugle.gyurun.core.datastore.di.dataStoreModule
import com.gyugle.gyurun.core.network.di.networkModule
import com.gyugle.gyurun.di.appModule
import com.gyugle.gyurun.feature.active.presentation.impl.di.activePresentationModule
import com.gyugle.gyurun.feature.details.presentation.impl.di.detailsPresentationModule
import com.gyugle.gyurun.feature.onboarding.presentation.impl.di.onboardingPresentationModule
import com.gyugle.gyurun.feature.overview.presentation.impl.di.overviewPresentationModule
import com.gyugle.gyurun.feature.stats.presentation.impl.di.statsPresentationModule
import com.gyugle.gyurun.run.location.di.locationModule
import com.gyugle.gyurun.run.sensor.di.sensorModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import timber.log.Timber

class GyuRunApp :
    Application(),
    KoinComponent {
    override fun onCreate() {
        super.onCreate()

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
                onboardingPresentationModule,
                overviewPresentationModule,
                activePresentationModule,
                detailsPresentationModule,
                statsPresentationModule,
                appModule
            )
        }

        Timber.i("GyuRun 시작 - DI 컨테이너 준비 완료ㅕ")
    }
}