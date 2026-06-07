package app.devper.pharm.data.storage

import app.devper.pharm.common.platform.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val KEY_TOKEN = "auth.token"

class TokenStorage(private val secureStorage: SecureStorage) {
    private val _tokenFlow = MutableStateFlow(secureStorage.get(KEY_TOKEN))
    val tokenFlow: StateFlow<String?> = _tokenFlow.asStateFlow()

    val token: String? get() = _tokenFlow.value

    fun save(value: String) {
        secureStorage.put(KEY_TOKEN, value)
        _tokenFlow.value = value
    }

    fun clear() {
        secureStorage.remove(KEY_TOKEN)
        _tokenFlow.value = null
    }
}
