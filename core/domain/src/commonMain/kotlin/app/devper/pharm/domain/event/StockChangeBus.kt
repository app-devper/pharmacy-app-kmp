package app.devper.pharm.domain.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class StockChangeBus {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 8)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    fun emit() {
        _events.tryEmit(Unit)
    }
}
