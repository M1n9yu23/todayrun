package com.gyugle.gyurun.core.domain.run

enum class DistanceUnit(
    val metersPerUnit: Double,
    val symbol: String,
    val speedLabel: String
) {
    KILOMETERS(metersPerUnit = 1_000.0, symbol = "km", speedLabel = "km/h"),
    MILES(metersPerUnit = 1_609.344, symbol = "mi", speedLabel = "mph"),
}