package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.LoginRequest
import app.devper.pharm.data.remote.dto.LoginResponse
import app.devper.pharm.data.remote.dto.PasskeyLoginOptionsRequest
import app.devper.pharm.data.remote.dto.PasskeyLoginRequest
import app.devper.pharm.data.remote.dto.PasskeyOptionsResponse
import app.devper.pharm.data.remote.dto.SystemResponse
import app.devper.pharm.data.remote.dto.TicketRequest
import app.devper.pharm.data.remote.dto.UserInfoResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun login(request: LoginRequest): LoginResponse =
        client.post(config.umAuthLogin) { setBody(request) }.body()

    suspend fun exchange(request: TicketRequest): LoginResponse =
        client.post(config.umAuthExchange) { setBody(request) }.body()

    suspend fun passkeyLoginOptions(request: PasskeyLoginOptionsRequest): PasskeyOptionsResponse =
        client.post(config.umPasskeyLoginOptions) { setBody(request) }.body()

    suspend fun passkeyLogin(request: PasskeyLoginRequest): LoginResponse =
        client.post(config.umPasskeyLogin) { setBody(request) }.body()

    suspend fun getSystem(): SystemResponse =
        client.get(config.umAuthSystem).body()

    suspend fun getUserInfo(): UserInfoResponse =
        client.get(config.umAuthInfo).body()

    suspend fun logout() {
        client.post(config.umAuthLogout)
    }
}
