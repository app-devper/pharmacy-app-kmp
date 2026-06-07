package app.devper.pharm.platform

import app.devper.pharm.common.platform.SecureStorage
import kotlinx.browser.window

class WebSecureStorage : SecureStorage {

    private val storage get() = window.sessionStorage

    override fun put(key: String, value: String) {
        storage.setItem(key, value)
    }

    override fun get(key: String): String? = storage.getItem(key)

    override fun remove(key: String) {
        storage.removeItem(key)
    }
}
