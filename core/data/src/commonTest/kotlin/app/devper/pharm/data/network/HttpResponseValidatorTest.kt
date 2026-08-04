package app.devper.pharm.data.network

import app.devper.pharm.common.AuthException
import app.devper.pharm.common.ConflictException
import app.devper.pharm.common.ForbiddenException
import app.devper.pharm.common.NotFoundException
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
) = run {
    val tokenStorage = TokenStorage(InMemorySecureStorage()).apply { save("preset-token") }
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
