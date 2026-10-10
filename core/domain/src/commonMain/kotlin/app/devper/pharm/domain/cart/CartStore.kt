package app.devper.pharm.domain.cart

import app.devper.pharm.domain.model.ActiveCart
import app.devper.pharm.domain.model.ParkedCart

interface CartStore {
    fun loadActive(): ActiveCart?
    fun saveActive(active: ActiveCart)
    fun clearActive()
    fun loadParked(): List<ParkedCart?>
    fun saveParked(slot: Int, parked: ParkedCart)
    fun clearParked(slot: Int)
}
