package app.devper.pharm.data.storage

import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.CartDiscount
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Customer
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ParkedCart
import app.devper.pharm.domain.repository.PARK_SLOT_COUNT
import com.russhwolf.settings.Settings
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class ParkedCartStorage(private val settings: Settings) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun loadAll(): List<ParkedCart?> = (0 until PARK_SLOT_COUNT).map { load(it) }

    fun load(slot: Int): ParkedCart? {
        val raw = settings.getStringOrNull(keyFor(slot)) ?: return null
        return try {
            json.decodeFromString(ParkedCartDto.serializer(), raw)
                .takeIf { it.version == ParkedCartDto.SCHEMA_VERSION }
                ?.toDomain()
        } catch (_: SerializationException) {

            settings.remove(keyFor(slot))
            null
        }
    }

    fun save(slot: Int, parked: ParkedCart) {
        val raw = json.encodeToString(ParkedCartDto.serializer(), parked.toDto())
        settings.putString(keyFor(slot), raw)
    }

    fun clear(slot: Int) {
        settings.remove(keyFor(slot))
    }

    private fun keyFor(slot: Int) = "cart.park.slot.$slot"
}

private fun ParkedCart.toDto() = ParkedCartDto(
    items = items.map { it.toDto() },
    customer = customer?.toDto(),
    cartDiscount = cartDiscount.toDto(),
    activeTier = activeTier,
    cashReceived = cashReceived,
    parkedAt = parkedAt,
)

private fun ParkedCartDto.toDomain() = ParkedCart(
    items = items.map { it.toDomain() },
    customer = customer?.toDomain(),
    cartDiscount = cartDiscount.toDomain(),
    activeTier = activeTier,
    cashReceived = cashReceived,
    parkedAt = parkedAt,
)

private fun CartLine.toDto() = ParkedCartLineDto(
    drug = drug.toDto(),
    qty = qty,
    tier = tier,
    discount = discount,
    selectedUnit = selectedUnit?.toDto(),
)

private fun ParkedCartLineDto.toDomain() = CartLine(
    drug = drug.toDomain(),
    qty = qty,
    tier = tier,
    discount = discount,
    selectedUnit = selectedUnit?.toDomain(),
)

private fun Drug.toDto() = ParkedDrugDto(
    id = id,
    name = name,
    genericName = genericName,
    barcode = barcode,
    regNo = regNo,
    unit = unit,
    sellPrice = sellPrice,
    stock = stock,
    prices = prices,
    altUnits = altUnits.map { it.toDto() },
    reportTypes = reportTypes,
)

private fun ParkedDrugDto.toDomain() = Drug(
    id = id,
    name = name,
    genericName = genericName,
    type = null,
    strength = null,
    barcode = barcode,
    sellPrice = sellPrice,
    costPrice = 0.0,
    stock = stock,
    minStock = 0,
    unit = unit,
    regNo = regNo,
    prices = prices,
    altUnits = altUnits.map { it.toDomain() },
    reportTypes = reportTypes,
)

private fun AltUnit.toDto() = ParkedAltUnitDto(
    name = name,
    factor = factor,
    sellPrice = sellPrice,
    prices = prices,
    barcode = barcode,
    hidden = hidden,
)

private fun ParkedAltUnitDto.toDomain() = AltUnit(
    name = name,
    factor = factor,
    sellPrice = sellPrice,
    prices = prices,
    barcode = barcode,
    hidden = hidden,
)

private fun Customer.toDto() = ParkedCustomerDto(
    id = id,
    name = name,
    phone = phone,
    priceTier = priceTier,
    allergyNote = allergyNote,
)

private fun ParkedCustomerDto.toDomain() = Customer(
    id = id,
    name = name,
    phone = phone,
    priceTier = priceTier,
    allergyNote = allergyNote,
)

private fun CartDiscount.toDto(): ParkedDiscountDto = when (this) {
    is CartDiscount.None    -> ParkedDiscountDto(kind = "none", value = 0.0)
    is CartDiscount.Flat    -> ParkedDiscountDto(kind = "flat", value = amount)
    is CartDiscount.Percent -> ParkedDiscountDto(kind = "percent", value = percent)
}

private fun ParkedDiscountDto.toDomain(): CartDiscount = when (kind) {
    "flat"    -> CartDiscount.Flat(value)
    "percent" -> CartDiscount.Percent(value)
    else      -> CartDiscount.None
}
