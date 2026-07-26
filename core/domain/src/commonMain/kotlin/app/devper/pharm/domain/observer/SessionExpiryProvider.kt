package app.devper.pharm.domain.observer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionExpiryProvider {
    private val expired = MutableStateFlow(false)
    val state: StateFlow<Boolean> = expired.asStateFlow()

    fun markExpired() {
        expired.value = true
    }

    fun acknowledge() {
        expired.value = false
    }
}
