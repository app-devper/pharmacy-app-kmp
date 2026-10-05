package app.devper.pharm.domain.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StockChangeBus {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 8)
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    private val _generation = MutableStateFlow(0)
    val generation: StateFlow<Int> = _generation.asStateFlow()

    fun emit() {
        _generation.update { it + 1 }
        _events.tryEmit(Unit)
    }
}
