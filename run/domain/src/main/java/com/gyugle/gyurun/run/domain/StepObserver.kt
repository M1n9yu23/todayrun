package com.gyugle.gyurun.run.domain

import kotlinx.coroutines.flow.Flow

interface StepObserver {
    fun observeSteps(): Flow<Int>
}