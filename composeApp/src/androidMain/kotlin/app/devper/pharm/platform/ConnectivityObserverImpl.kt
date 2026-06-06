package app.devper.pharm.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import app.devper.pharm.common.platform.ConnectivityObserver
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

class ConnectivityObserverImpl(private val context: Context) : ConnectivityObserver {

    override val online: Flow<Boolean> = callbackFlow {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        fun hasInternet(): Boolean {
            val capabilities = manager.getNetworkCapabilities(manager.activeNetwork) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

        trySend(hasInternet())

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(hasInternet())
            }
        }

        manager.registerDefaultNetworkCallback(callback)
        awaitClose { manager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged().conflate()
}
