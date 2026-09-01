package com.gyugle.gyurun.core.domain.run.usecase

import com.gyugle.gyurun.core.domain.run.Run
import com.gyugle.gyurun.core.domain.run.WeeklyDistance
import com.gyugle.gyurun.core.domain.run.calculator.WeeklyDistanceCalculator

class GetWeeklyDistancesUseCase {
    operator fun invoke(runs: List<Run>): List<WeeklyDistance> = WeeklyDistanceCalculator.calculate(runs)
}
