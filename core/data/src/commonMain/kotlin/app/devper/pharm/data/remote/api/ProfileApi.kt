package app.devper.pharm.data.remote.api

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.remote.dto.ChangePasswordRequest
import app.devper.pharm.data.remote.dto.UmUserDto
import app.devper.pharm.data.remote.dto.UpdateProfileRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class ProfileApi(
    private val client: HttpClient,
    private val config: ApiConfig,
) {

    suspend fun getMyInfo(): UmUserDto =
        client.get(config.umUser("/info")).body()

    suspend fun updateMyInfo(request: UpdateProfileRequest): UmUserDto =
        client.put(config.umUser("/info")) { setBody(request) }.body()

    suspend fun changeMyPassword(request: ChangePasswordRequest) {
        client.put(config.umUser("/change-password")) { setBody(request) }
    }
}
