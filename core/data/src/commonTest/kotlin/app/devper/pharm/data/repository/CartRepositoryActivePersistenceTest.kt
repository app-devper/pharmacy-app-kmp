package app.devper.pharm.data.repository

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import app.devper.pharm.data.storage.MemorySettings
import app.devper.pharm.data.storage.ParkedCartStorage
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.AddCartItemParam
import com.russhwolf.settings.Settings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CartRepositoryActivePersistenceTest {

    private fun drug(id: String = "d1") = Drug(
        id = id, name = "Paracetamol", genericName = null, type = null, strength = null,
        barcode = null, sellPrice = Money(5.0), costPrice = Money(2.0), stock = Quantity(100), minStock = Quantity(0),
        unit = "เม็ด", regNo = null,
    )

    private fun repo(settings: Settings) = CartRepositoryImpl(ParkedCartStorage(settings))

    @Test
    fun active_cart_survives_a_fresh_instance() {
        val settings = MemorySettings()
        repo(settings).add(AddCartItemParam(drug = drug(), altUnit = null))

        val reborn = repo(settings)
        val items = reborn.state.value.active.items
        assertEquals(1, items.size)
        assertEquals("d1", items[0].drug.id)
    }

    @Test
    fun clear_removes_the_persisted_active_cart() {
        val settings = MemorySettings()
        val first = repo(settings)
        first.add(AddCartItemParam(drug = drug(), altUnit = null))
        first.clear()

        assertTrue(repo(settings).state.value.active.items.isEmpty())
    }

    @Test
    fun commit_receipt_removes_the_persisted_active_cart() {
        val settings = MemorySettings()
        val first = repo(settings)
        first.add(AddCartItemParam(drug = drug(), altUnit = null))
        first.commitReceipt(Sale("s1", "B1", 10.0, 0.0, 0.0, emptyList()))

        assertTrue(repo(settings).state.value.active.items.isEmpty())
    }
}
