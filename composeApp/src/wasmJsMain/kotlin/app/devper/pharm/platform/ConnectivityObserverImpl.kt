package app.devper.pharm.platform

import app.devper.pharm.common.platform.ConnectivityObserver
import kotlinx.browser.window
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.w3c.dom.events.Event

class ConnectivityObserverImpl : ConnectivityObserver {

    override val online: Flow<Boolean> = callbackFlow {
        trySend(window.navigator.onLine)
        val listener: (Event) -> Unit = { trySend(window.navigator.onLine) }
        window.addEventListener("online", listener)
        window.addEventListener("offline", listener)
        awaitClose {
            window.removeEventListener("online", listener)
            window.removeEventListener("offline", listener)
        }
    }.distinctUntilChanged()
}
