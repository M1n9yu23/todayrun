package com.gyugle.gyurun.core.network

import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.ContentConvertException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import timber.log.Timber

suspend inline fun <reified Response : Any> HttpClient.get(
    route: String,
    queryParameters: Map<String, Any?> = mapOf(),
): Result<Response, DataError.Network> =
    safeCall {
        get {
            url(constructRoute(route))
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, DataError.Network> =
    try {
        responseToResult(execute())
    } catch (e: UnresolvedAddressException) {
        Timber.e(e, "네트워크에 닿지 못했다")
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SerializationException) {
        Timber.e(e, "응답을 해석하지 못했다")
        Result.Error(DataError.Network.SERIALIZATION)
    } catch (e: ContentConvertException) {
        Timber.e(e, "응답의 모양이 우리가 아는 모양이 아니다")
        Result.Error(DataError.Network.SERIALIZATION)
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        Timber.e(e, "예상치 못한 네트워크 오류")
        Result.Error(DataError.Network.UNKNOWN)
    }

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> =
    when (response.status.value) {
        in 200..299 -> Result.Success(response.body<T>())
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }

fun constructRoute(route: String): String {
    val baseUrl = BuildConfig.BASE_URL
    check(baseUrl.isNotBlank()) {
        "BASE_URL 이 비어 있다 — local.properties 에 BASE_URL 을 적어야 한다"
    }
    return when {
        route.startsWith(baseUrl) -> route
        route.startsWith("/") -> baseUrl + route
        else -> "$baseUrl/$route"
    }
}
