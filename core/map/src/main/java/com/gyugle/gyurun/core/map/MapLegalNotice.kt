package com.gyugle.gyurun.core.map

data class MapLegalNotice(
    val provider: MapProvider,
    val noticeUrl: String,
)

internal fun mapLegalNoticeOf(provider: MapProvider): MapLegalNotice =
    when (provider) {
        MapProvider.GOOGLE -> {
            MapLegalNotice(
                provider = MapProvider.GOOGLE,
                noticeUrl = "https://www.google.com/help/legalnotices_maps/",
            )
        }

        MapProvider.KAKAO -> {
            MapLegalNotice(
                provider = MapProvider.KAKAO,
                noticeUrl = "https://apis.map.kakao.com/android_v2/license/",
            )
        }
    }

val activeMapLegalNotice: MapLegalNotice = mapLegalNoticeOf(activeMapProvider)
