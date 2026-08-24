package com.gyugle.gyurun.core.network

import android.security.NetworkSecurityPolicy
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import timber.log.Timber

private fun cleartextGuard(isCleartextPermitted: (String) -> Boolean) =
    createClientPlugin("CleartextGuard") {
        onRequest { request, _ ->
            val host = request.url.host
            val usesTls = request.url.protocol == URLProtocol.HTTPS
            if (!usesTls && !isCleartextPermitted(host)) {
                throw SecurityException("차단된 평문 요청: $host 로의 HTTP는 네트워크 보안 정책이 허용하지 않는다")
            }
        }
    }

class HttpClientFactory(
    private val isCleartextPermitted: (String) -> Boolean = { host ->
        NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted(host)
    },
) {
    fun build(engine: HttpClientEngine): HttpClient =
        HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    json =
                        Json {
                            ignoreUnknownKeys = true
                        },
                )
            }
            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
                requestTimeoutMillis = 15_000
                socketTimeoutMillis = 15_000
            }
            install(cleartextGuard(isCleartextPermitted))
            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            Timber.tag("Ktor").d(message)
                        }
                    }
                level = LogLevel.ALL
            }
        }
}