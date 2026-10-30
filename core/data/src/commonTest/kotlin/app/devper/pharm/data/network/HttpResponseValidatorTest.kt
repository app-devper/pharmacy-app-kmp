package app.devper.pharm.data.network

import app.devper.pharm.common.AuthException
import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.common.IdentityUnavailableException
import app.devper.pharm.common.NotFoundException
import app.devper.pharm.common.PasskeyExpiredException
import app.devper.pharm.common.PasskeyRejectedException
import app.devper.pharm.common.PasskeysUnavailableException
import app.devper.pharm.common.ServerException
import app.devper.pharm.domain.observer.SessionExpiryProvider
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.data.storage.InMemorySecureStorage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private fun mockClientReturning(
    status: HttpStatusCode,
    body: String = "",
    sessionExpiry: SessionExpiryProvider = SessionExpiryProvider(),
    token: String? = "preset-token",
) = run {
    val tokenStorage = TokenStorage(InMemorySecureStorage()).apply { token?.let(::save) }
    val engineFactory = object : io.ktor.client.engine.HttpClientEngineFactory<io.ktor.client.engine.mock.MockEngineConfig> {
        override fun create(block: io.ktor.client.engine.mock.MockEngineConfig.() -> Unit): io.ktor.client.engine.HttpClientEngine {
            val config = io.ktor.client.engine.mock.MockEngineConfig().apply {
                addHandler { respond(content = body, status = status, headers = headersOf()) }
            }
            return MockEngine(config)
        }
    }
    buildHttpClient(engineFactory, tokenStorage, sessionExpiry, enableLogging = false, installTimeout = false) to tokenStorage
}

class HttpResponseValidatorTest {

    @Test
    fun identity_503_throws_IdentityUnavailableException_and_keeps_the_session() = runTest {
        val sessionExpiry = SessionExpiryProvider()
        val (client, tokenStorage) = mockClientReturning(
            HttpStatusCode.ServiceUnavailable,
            body = "{\"error\":\"identity service unavailable\"}\n",
            sessionExpiry = sessionExpiry,
        )
        assertFailsWith<IdentityUnavailableException> { client.get("https://example.test/x") }
        assertEquals("preset-token", tokenStorage.token)
        assertFalse(sessionExpiry.state.value)
    }

    @Test
    fun other_503_stays_a_ServerException() = runTest {
        val (client, _) = mockClientReturning(HttpStatusCode.ServiceUnavailable, body = "upstream down")
        val e = assertFailsWith<ServerException> { client.get("https://example.test/x") }
        assertEquals(503, e.statusCode)
    }

    @Test
    fun status_401_throws_AuthException_and_clears_token() = runTest {
        val (client, tokenStorage) = mockClientReturning(HttpStatusCode.Unauthorized)
        assertFailsWith<AuthException> { client.get("https://example.test/x") }
        assertEquals(null, tokenStorage.token)
    }

    @Test
    fun status_401_marks_the_session_expired() = runTest {
        val sessionExpiry = SessionExpiryProvider()
        val (client, _) = mockClientReturning(HttpStatusCode.Unauthorized, sessionExpiry = sessionExpiry)
        assertFailsWith<AuthException> { client.get("https://example.test/x") }
        assertTrue(sessionExpiry.state.value)
    }

    @Test
    fun status_401_on_a_request_without_a_token_is_a_refused_sign_in_not_an_expired_session() = runTest {
        val sessionExpiry = SessionExpiryProvider()
        val (client, _) = mockClientReturning(HttpStatusCode.Unauthorized, sessionExpiry = sessionExpiry, token = null)
        assertFailsWith<AuthException> { client.get("https://example.test/auth/exchange") }
        assertFalse(sessionExpiry.state.value)
    }

    @Test
    fun a_refused_passkey_is_typed_and_keeps_the_session() = runTest {
        val sessionExpiry = SessionExpiryProvider()
        val (client, tokenStorage) = mockClientReturning(
            HttpStatusCode.Unauthorized,
            body = "{\"code\":\"UM-401-005\",\"message\":\"passkey not accepted\"}",
            sessionExpiry = sessionExpiry,
        )
        assertFailsWith<PasskeyRejectedException> { client.get("https://example.test/auth/passkey/login") }
        assertEquals("preset-token", tokenStorage.token)
        assertFalse(sessionExpiry.state.value)
    }

    @Test
    fun an_expired_passkey_ceremony_is_typed() = runTest {
        val (client, _) = mockClientReturning(HttpStatusCode.BadRequest, body = "{\"code\":\"UM-400-006\"}")
        assertFailsWith<PasskeyExpiredException> { client.get("https://example.test/auth/passkey/login") }
    }

    @Test
    fun passkeys_disabled_on_the_server_is_typed() = runTest {
        val (client, _) = mockClientReturning(HttpStatusCode.NotFound, body = "{\"code\":\"UM-404-002\"}")
        assertFailsWith<PasskeysUnavailableException> { client.get("https://example.test/auth/passkey/login/options") }
    }

    @Test
    fun other_failures_do_not_mark_the_session_expired() = runTest {
        val sessionExpiry = SessionExpiryProvider()
        val (client, _) = mockClientReturning(HttpStatusCode.Forbidden, sessionExpiry = sessionExpiry)
        assertFailsWith<ForbiddenException> { client.get("https://example.test/x") }
        assertFalse(sessionExpiry.state.value)
    }

    @Test
    fun status_403_throws_ForbiddenException() = runTest {
        val (client, _) = mockClientReturning(HttpStatusCode.Forbidden)
        assertFailsWith<ForbiddenException> { client.get("https://example.test/x") }
    }

    @Test
    fun status_404_throws_NotFoundException() = runTest {
        val (client, _) = mockClientReturning(HttpStatusCode.NotFound)
        assertFailsWith<NotFoundException> { client.get("https://example.test/x") }
    }

    @Test
    fun status_409_throws_ConflictException_with_payload() = runTest {
        val payload = "{\"err\":\"duplicate\"}"
        val (client, _) = mockClientReturning(HttpStatusCode.Conflict, body = payload)
        val ex = assertFailsWith<ConflictException> { client.get("https://example.test/x") }
        assertEquals(payload, ex.payload)
    }

    @Test
    fun status_503_throws_ServerException_with_status_and_body() = runTest {
        val (client, _) = mockClientReturning(HttpStatusCode.ServiceUnavailable, body = "down")
        val ex = assertFailsWith<ServerException> { client.get("https://example.test/x") }
        assertEquals(503, ex.statusCode)
        assertEquals("down", ex.body)
        assertNotNull(ex.message)
    }

    @Test
    fun status_400_throws_ServerException_with_400_status() = runTest {
        val (client, _) = mockClientReturning(HttpStatusCode.BadRequest, body = "bad")
        val ex = assertFailsWith<ServerException> { client.get("https://example.test/x") }
        assertEquals(400, ex.statusCode)
        assertEquals("bad", ex.body)
    }
}
