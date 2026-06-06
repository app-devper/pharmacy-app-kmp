package app.devper.pharm.common.platform

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    val online: Flow<Boolean>
}
