package app.devper.pharm.domain.observer

import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.RolePermissions
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.model.permissions
import app.devper.pharm.domain.repository.profile.ProfileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignedInUser(private val profile: ProfileRepository) {

    private val current = MutableStateFlow<UmUser?>(null)

    val user: StateFlow<UmUser?> = current.asStateFlow()

    suspend fun load(): UmUser = profile.get().also { current.value = it }

    suspend fun get(): UmUser = current.value ?: load()

    suspend fun role(): Role = try {
        get().role
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Role.UNKNOWN
    }

    suspend fun permissions(): RolePermissions = role().permissions()

    suspend fun displayNameOrBlank(): String = try {
        get().displayName
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ""
    }

    fun replace(user: UmUser) {
        current.value = user
    }

    fun clear() {
        current.value = null
    }
}
