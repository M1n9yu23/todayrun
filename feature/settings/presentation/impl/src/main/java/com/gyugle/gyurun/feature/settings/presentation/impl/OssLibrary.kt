package com.gyugle.gyurun.feature.settings.presentation.impl

internal data class OssLibrary(
    val name: String,
    val license: String,
    val url: String,
)

internal val appLibraries: List<OssLibrary> =
    listOf(
        OssLibrary(
            name = "Jetpack Compose",
            license = "Apache 2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/compose",
        ),
        OssLibrary(
            name = "AndroidX Navigation 3",
            license = "Apache 2.0",
            url = "https://developer.android.com/guide/navigation",
        ),
        OssLibrary(
            name = "Koin",
            license = "Apache 2.0",
            url = "https://github.com/InsertKoinIO/koin",
        ),
        OssLibrary(
            name = "Ktor",
            license = "Apache 2.0",
            url = "https://github.com/ktorio/ktor",
        ),
        OssLibrary(
            name = "kotlinx.serialization",
            license = "Apache 2.0",
            url = "https://github.com/Kotlin/kotlinx.serialization",
        ),
        OssLibrary(
            name = "kotlinx.coroutines",
            license = "Apache 2.0",
            url = "https://github.com/Kotlin/kotlinx.coroutines",
        ),
        OssLibrary(
            name = "Room",
            license = "Apache 2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/room",
        ),
        OssLibrary(
            name = "Jetpack DataStore",
            license = "Apache 2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/datastore",
        ),
        OssLibrary(
            name = "Paging 3",
            license = "Apache 2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/paging",
        ),
        OssLibrary(
            name = "Coil",
            license = "Apache 2.0",
            url = "https://github.com/coil-kt/coil",
        ),
        OssLibrary(
            name = "WorkManager",
            license = "Apache 2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/work",
        ),
        OssLibrary(
            name = "Glance",
            license = "Apache 2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/glance",
        ),
        OssLibrary(
            name = "Timber",
            license = "Apache 2.0",
            url = "https://github.com/JakeWharton/timber",
        ),
        OssLibrary(
            name = "Pretendard",
            license = "SIL Open Font License 1.1",
            url = "https://github.com/orioncactus/pretendard",
        ),
    )