package app.devper.pharm.data.network

import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.common.AppException
import app.devper.pharm.common.AuthException
import app.devper.pharm.domain.observer.SessionExpiryProvider
import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.common.IdentityUnavailableException
import app.devper.pharm.common.NetworkException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.common.PasskeyExpiredException
import app.devper.pharm.common.PasskeyRejectedException
import app.devper.pharm.common.PasskeysUnavailableException
import app.devper.pharm.common.ServerException
import app.devper.pharm.data.remote.dto.UmErrorResponse
import kotlin.coroutines.cancellation.CancellationException
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val CONNECT_TIMEOUT_MS = 15_000L
private const val REQUEST_TIMEOUT_MS = 30_000L
private const val SOCKET_TIMEOUT_MS = 30_000L

fun <T : HttpClientEngineConfig> buildHttpClient(
    engine: HttpClientEngineFactory<T>,
    tokenStorage: TokenStorage,
    sessionExpiry: SessionExpiryProvider,
    json: Json = AppJson,
    enableLogging: Boolean = false,
    installTimeout: Boolean = true,
): HttpClient = HttpClient(engine) {
    expectSuccess = false

    install(ContentNegotiation) {
        json(json)
    }

    install(DefaultRequest) {
        contentType(ContentType.Application.Json)
        val token = tokenStorage.token
        if (!token.isNullOrBlank()) {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    if (installTimeout) {
        install(HttpTimeout) {
            connectTimeoutMillis = CONNECT_TIMEOUT_MS
            requestTimeoutMillis = REQUEST_TIMEOUT_MS
            socketTimeoutMillis = SOCKET_TIMEOUT_MS
        }
    }

    if (enableLogging) {
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
    }

    HttpResponseValidator {
        validateResponse { response ->
            val status = response.status
            if (status.isSuccess()) return@validateResponse
            val body = response.bodyAsText()
            val code = umErrorCode(json, body)
            throw when {
                code == UM_PASSKEY_REJECTED -> PasskeyRejectedException()
                code == UM_PASSKEY_EXPIRED -> PasskeyExpiredException()
                code == UM_PASSKEYS_DISABLED -> PasskeysUnavailableException()
                status == HttpStatusCode.Unauthorized -> {
                    if (response.request.headers.contains(HttpHeaders.Authorization)) {
                        tokenStorage.clear()
                        sessionExpiry.markExpired()
                    }
                    AuthException()
                }
                status == HttpStatusCode.Forbidden -> ForbiddenException()
                status == HttpStatusCode.NotFound -> NotFoundException()
                status == HttpStatusCode.Conflict -> ConflictException(payload = body)
                else -> if (status == HttpStatusCode.ServiceUnavailable &&
                    body.contains(IDENTITY_UNAVAILABLE_ERROR)
                ) IdentityUnavailableException() else ServerException(
                    message = if (status.value in 500..599) "Server error (${status.value})"
                              else "HTTP error (${status.value})",
                    statusCode = status.value,
                    body = body,
                )
            }
        }
        handleResponseExceptionWithRequest { cause, _ ->
            if (cause is CancellationException) throw cause
            if (cause is AppException) throw cause
            throw NetworkException(cause = cause)
        }
    }
}

private const val IDENTITY_UNAVAILABLE_ERROR = "identity service unavailable"
private const val UM_PASSKEY_REJECTED = "UM-401-005"
private const val UM_PASSKEY_EXPIRED = "UM-400-006"
private const val UM_PASSKEYS_DISABLED = "UM-404-002"

private fun HttpStatusCode.isSuccess(): Boolean = value in 200..299

private fun umErrorCode(json: Json, body: String): String? =
    runCatching { json.decodeFromString(UmErrorResponse.serializer(), body).code }.getOrNull()

val AppJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = false
    explicitNulls = false
}
