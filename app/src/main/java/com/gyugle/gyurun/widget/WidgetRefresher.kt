package com.gyugle.gyurun.widget

import com.gyugle.gyurun.core.domain.run.DistanceUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

internal class WidgetRefresher(
    private val runsChanged: Flow<*>,
    private val distanceUnit: Flow<DistanceUnit>,
    private val applicationScope: CoroutineScope,
    private val redrawWidget: suspend () -> Unit,
) {
    fun start() {
        combine(runsChanged, distanceUnit.distinctUntilChanged()) { _, _ -> Unit }
            .onEach { redrawOrLog() }
            .catch { throwable -> Timber.w(throwable, "위젯이 볼 통로가 끊겼다") }
            .launchIn(applicationScope)
    }

    private suspend fun redrawOrLog() {
        try {
            redrawWidget()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Timber.w(e, "위젯을 다시 그리기 실패")
        }
    }
}