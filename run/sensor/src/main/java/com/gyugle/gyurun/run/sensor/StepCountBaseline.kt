package com.gyugle.gyurun.run.sensor

internal class StepCountBaseline {
    private var initialStepCount: Int? = null

    fun stepsSinceStart(totalStepsSinceBoot: Int): Int {
        val baseline =
            initialStepCount ?: totalStepsSinceBoot.also {
                initialStepCount = it
            }
        return totalStepsSinceBoot - baseline
    }
}
