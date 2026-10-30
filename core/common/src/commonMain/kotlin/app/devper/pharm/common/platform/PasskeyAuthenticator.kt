package app.devper.pharm.common.platform

import app.devper.pharm.common.UnsupportedPlatformException

interface PasskeyAuthenticator {
    val isSupported: Boolean

    suspend fun get(optionsJson: String): String

    object Unsupported : PasskeyAuthenticator {
        override val isSupported: Boolean = false
        override suspend fun get(optionsJson: String): String = throw UnsupportedPlatformException()
    }
}
