package app.devper.pharm.data.repository

import app.devper.pharm.common.LaunchExpiredException
import app.devper.pharm.common.LaunchRefusedException
import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.remote.api.AuthApi
import app.devper.pharm.data.storage.InMemorySecureStorage
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.domain.observer.SessionExpiryProvider
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val USER_BODY = "{\"id\":\"u1\",\"username\":\"somsri\",\"firstName\":\"Somsri\",\"lastName\":\"S\",\"role\":\"USER\"}"

private fun systemBody(code: String) = "{\"id\":\"s1\",\"clientId\":\"123\",\"systemName\":\"Pharmacy\",\"systemCode\":\"$code\",\"host\":\"https://api.test\"}"

private class Harness(private val route: (path: String, body: String) -> Pair<HttpStatusCode, String>) {
    val sent = mutableMapOf<String, String>()
    val tokenStorage = TokenStorage(InMemorySecureStorage())
    val repo: AuthRepositoryImpl

    init {
        val factory = object : HttpClientEngineFactory<MockEngineConfig> {
            override fun create(block: MockEngineConfig.() -> Unit): HttpClientEngine = MockEngine(
                MockEngineConfig().apply {
                    addHandler { request ->
                        val body = bodyOf(request)
                        sent[request.url.encodedPath] = body
                        val (status, answer) = route(request.url.encodedPath, body)
                        respond(content = answer, status = status, headers = headersOf("Content-Type", "application/json"))
                    }
                },
            )
        }
        val client = buildHttpClient(factory, tokenStorage, SessionExpiryProvider(), installTimeout = false)
        repo = AuthRepositoryImpl(AuthApi(client, ApiConfig("http://um.test")), tokenStorage)
    }

    private fun bodyOf(request: HttpRequestData): String = (request.body as? TextContent)?.text ?: ""
}

private fun umWith(system: String) = Harness { path, _ ->
    when {
        path.endsWith("/auth/exchange") -> HttpStatusCode.OK to "{\"accessToken\":\"tok.sso\"}"
        path.endsWith("/auth/system") -> HttpStatusCode.OK to systemBody(system)
        path.endsWith("/user/info") -> HttpStatusCode.OK to USER_BODY
        path.endsWith("/auth/passkey/login/options") -> HttpStatusCode.OK to "{\"ceremonyId\":\"c1\",\"options\":{\"publicKey\":{\"challenge\":\"x\"}}}"
        path.endsWith("/auth/passkey/login") -> HttpStatusCode.OK to "{\"accessToken\":\"tok.pk\"}"
        else -> HttpStatusCode.NotFound to ""
    }
}

class AuthRepositoryImplTest {

    @Test
    fun a_ticket_from_um_starts_the_session_replacing_an_older_one() = runTest {
        val h = umWith("PHARMACY")
        h.tokenStorage.save("tok.someone-else")

        val user = h.repo.exchangeTicket("t-1")

        assertEquals("somsri", user.username)
        assertEquals("tok.sso", h.tokenStorage.token)
        assertEquals("{\"ticket\":\"t-1\"}", h.sent["/api/um/v1/auth/exchange"])
    }

    @Test
    fun a_ticket_for_another_system_is_refused_and_leaves_no_token() = runTest {
        val h = umWith("GOLD")
        h.tokenStorage.save("tok.someone-else")

        assertFailsWith<LaunchRefusedException> { h.repo.exchangeTicket("t-1") }
        assertNull(h.tokenStorage.token)
    }

    @Test
    fun an_expired_ticket_is_a_LaunchExpiredException_and_ends_the_older_session() = runTest {
        val h = Harness { _, _ -> HttpStatusCode.Unauthorized to "{\"code\":\"UM-401-002\",\"message\":\"ticket invalid or expired\"}" }
        h.tokenStorage.save("tok.someone-else")

        assertFailsWith<LaunchExpiredException> { h.repo.exchangeTicket("old") }
        assertNull(h.tokenStorage.token)
    }

    @Test
    fun passkey_login_options_ask_um_for_the_pharmacy_system() = runTest {
        val h = umWith("PHARMACY")

        val challenge = h.repo.passkeyLoginOptions()

        assertEquals("c1", challenge.ceremonyId)
        assertTrue(challenge.optionsJson.contains("\"challenge\":\"x\""))
        assertEquals("{\"system\":\"PHARMACY\"}", h.sent["/api/um/v1/auth/passkey/login/options"])
    }

    @Test
    fun passkey_login_sends_the_credential_as_a_json_object_and_keeps_the_token() = runTest {
        val h = umWith("PHARMACY")

        val user = h.repo.loginWithPasskey("c1", "{\"id\":\"cred\",\"type\":\"public-key\"}")

        assertEquals("somsri", user.username)
        assertEquals("tok.pk", h.tokenStorage.token)
        assertEquals("{\"ceremonyId\":\"c1\",\"credential\":{\"id\":\"cred\",\"type\":\"public-key\"}}", h.sent["/api/um/v1/auth/passkey/login"])
    }
}
