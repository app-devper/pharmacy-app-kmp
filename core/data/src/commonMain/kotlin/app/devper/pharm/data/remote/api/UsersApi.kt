package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.CreateUserRequest
import app.devper.pharm.data.remote.dto.SetPasswordRequest
import app.devper.pharm.data.remote.dto.SetRoleRequest
import app.devper.pharm.data.remote.dto.SetStatusRequest
import app.devper.pharm.data.remote.dto.UmUserDto
import app.devper.pharm.data.remote.dto.UpdateUserRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class UsersApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun list(): List<UmUserDto> =
        client.get(config.umUser()).body()

    suspend fun create(request: CreateUserRequest): UmUserDto =
        client.post(config.umUser()) { setBody(request) }.body()

    suspend fun update(id: String, request: UpdateUserRequest): UmUserDto =
        client.put(config.umUser("/$id")) { setBody(request) }.body()

    suspend fun delete(id: String) {
        client.delete(config.umUser("/$id"))
    }

    suspend fun setRole(id: String, request: SetRoleRequest): UmUserDto =
        client.patch(config.umUser("/$id/role")) { setBody(request) }.body()

    suspend fun setStatus(id: String, request: SetStatusRequest): UmUserDto =
        client.patch(config.umUser("/$id/status")) { setBody(request) }.body()

    suspend fun setPassword(id: String, request: SetPasswordRequest) {
        client.patch(config.umUser("/$id/set-password")) { setBody(request) }
    }
}
