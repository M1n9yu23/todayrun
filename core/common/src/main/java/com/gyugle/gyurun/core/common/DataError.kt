package com.gyugle.gyurun.core.common

sealed interface DataError : Error {
    enum class Network : DataError {
        NO_INTERNET,
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        PAYLOAD_TOO_LARGE,
        TOO_MANY_REQUESTS,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
    }

    enum class Local : DataError {
        DISK_FULL,
    }
}
