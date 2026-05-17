package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.LoginRequest
import app.devper.pharm.data.remote.dto.LoginResponse
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

    suspend fun getUserInfo(): UserInfoResponse =
        client.get(config.umAuthInfo).body()

    suspend fun logout() {
        client.post(config.umAuthLogout)
    }
}
