package app.devper.pharm.data.network

import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.common.AppException
import app.devper.pharm.common.AuthException
import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.common.NetworkException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.common.ServerException
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
    json: Json = AppJson,
    enableLogging: Boolean = true,
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
            throw when (status) {
                HttpStatusCode.Unauthorized -> {

                    tokenStorage.clear()
                    AuthException()
                }
                HttpStatusCode.Forbidden     -> ForbiddenException()
                HttpStatusCode.NotFound      -> NotFoundException()
                HttpStatusCode.Conflict      -> ConflictException(payload = body)
                else -> ServerException(
                    message = if (status.value in 500..599) "เซิร์ฟเวอร์ขัดข้อง (${status.value})"
                              else "เกิดข้อผิดพลาด (${status.value})",
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

private fun HttpStatusCode.isSuccess(): Boolean = value in 200..299

val AppJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = false
    explicitNulls = false
}
