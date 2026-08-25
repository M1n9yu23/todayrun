package com.gyugle.gyurun.core.connectivity.domain.messaging

import com.gyugle.gyurun.core.common.Error

enum class MessagingError : Error {
    CONNECTION_INTERRUPTED,
    DISCONNECTED,
    UNKNOWN,
}