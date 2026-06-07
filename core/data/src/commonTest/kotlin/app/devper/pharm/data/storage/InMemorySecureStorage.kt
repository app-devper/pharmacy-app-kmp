package app.devper.pharm.data.storage

import app.devper.pharm.common.platform.SecureStorage

internal class InMemorySecureStorage : SecureStorage {
    private val store = mutableMapOf<String, String>()
    override fun put(key: String, value: String) { store[key] = value }
    override fun get(key: String): String? = store[key]
    override fun remove(key: String) { store.remove(key) }
}
