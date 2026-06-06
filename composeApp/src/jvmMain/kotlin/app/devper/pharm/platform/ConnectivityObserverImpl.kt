package app.devper.pharm.platform

import app.devper.pharm.common.platform.ConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ConnectivityObserverImpl : ConnectivityObserver {
    override val online: Flow<Boolean> = flowOf(true)
}
