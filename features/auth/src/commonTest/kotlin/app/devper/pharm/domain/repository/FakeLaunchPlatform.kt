package app.devper.pharm.domain.repository

import app.devper.pharm.common.PasskeyCancelledException
import app.devper.pharm.common.platform.LaunchTicket
import app.devper.pharm.common.platform.PasskeyAuthenticator

class FakePasskeyAuthenticator(
    override val isSupported: Boolean = true,
    private val cancel: Boolean = false,
) : PasskeyAuthenticator {
    val seenOptions = mutableListOf<String>()

    override suspend fun get(optionsJson: String): String {
        seenOptions += optionsJson
        if (cancel) throw PasskeyCancelledException()
        return "{\"id\":\"cred\"}"
    }
}

class FakeLaunchTicket(private var pending: String?) : LaunchTicket {
    override val isPending: Boolean get() = pending != null
    override fun peek(): String? = pending
    override fun consume() {
        pending = null
    }
}
