package app.devper.pharm.data.storage

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val KEY_TOKEN = "auth.token"

class TokenStorage(private val settings: Settings) {
    private val _tokenFlow = MutableStateFlow(settings.getStringOrNull(KEY_TOKEN))
    val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    val token: String? get() = _tokenFlow.value

    fun save(value: String) {
        settings.putString(KEY_TOKEN, value)
        _tokenFlow.value = value
    }

    fun clear() {
        settings.remove(KEY_TOKEN)
        _tokenFlow.value = null
    }
}
