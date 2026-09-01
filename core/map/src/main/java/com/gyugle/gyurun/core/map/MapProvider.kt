package com.gyugle.gyurun.core.map

enum class MapProvider {
    GOOGLE,
    KAKAO,
}

internal fun mapProviderOf(flavor: String): MapProvider =
    when (flavor) {
        "kakao" -> MapProvider.KAKAO
        else -> MapProvider.GOOGLE
    }

val activeMapProvider: MapProvider = mapProviderOf(BuildConfig.FLAVOR)
